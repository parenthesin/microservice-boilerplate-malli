(ns microservice-boilerplate.adapters
  (:require [microservice-boilerplate.schemas.db :as schemas.db]
            [microservice-boilerplate.schemas.types :as schemas.types]
            [microservice-boilerplate.schemas.wire-in :as schemas.wire-in]
            [microservice-boilerplate.schemas.wire-out :as schemas.wire-out])
  (:import [java.time ZoneId]
           [java.time.format DateTimeFormatter]))

(defn ^:private date->localdatetime
  {:malli/schema [:=> [:cat inst? schemas.types/JavaZoneId] schemas.types/JavaLocalDateTime]}
  [value zone-id]
  (-> value
      .toInstant
      (.atZone zone-id)
      .toLocalDateTime))

(defn inst->utc-formated-string
  {:malli/schema [:=> [:cat inst? :string] :string]}
  [inst str-format]
  (-> inst
      (date->localdatetime (ZoneId/of "UTC"))
      (.format (DateTimeFormatter/ofPattern str-format))))

(defn wire->usd-price
  {:malli/schema [:=> [:cat schemas.wire-out/CoinDeskResponse] number?]}
  [wire]
  (-> wire
      (get-in [:bpi :USD :rate_float])
      bigdec))

(defn ^:private wire-in->db
  {:malli/schema [:=> [:cat :uuid number? pos?] schemas.db/WalletTransaction]}
  [id btc usd]
  {:wallet/id id
   :wallet/btc_amount btc
   :wallet/usd_amount_at usd})

(defn deposit->db
  {:malli/schema [:=> [:cat :uuid pos? pos?] schemas.db/WalletTransaction]}
  [id btc usd]
  (wire-in->db id btc usd))

(defn withdrawal->db
  {:malli/schema [:=> [:cat :uuid neg? pos?] schemas.db/WalletTransaction]}
  [id btc usd]
  (wire-in->db id btc usd))

(defn db->wire-in
  {:malli/schema [:=> [:cat schemas.db/WalletEntry] schemas.wire-in/WalletEntry]}
  [{:wallet/keys [id btc_amount usd_amount_at created_at]}]
  {:id id
   :btc-amount (bigdec btc_amount)
   :usd-amount-at (bigdec usd_amount_at)
   :created-at created_at})

(defn ->wallet-history
  {:malli/schema [:=> [:cat pos? [:vector schemas.db/WalletEntry]] schemas.wire-in/WalletHistory]}
  [current-usd-price wallet-entries]
  (let [total-btc (reduce #(+ (:wallet/btc_amount %2) %1) 0M wallet-entries)]
    {:entries (mapv db->wire-in wallet-entries)
     :total-btc (bigdec total-btc)
     :total-current-usd (bigdec (* current-usd-price total-btc))}))
