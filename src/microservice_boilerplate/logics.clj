(ns microservice-boilerplate.logics
  (:require [microservice-boilerplate.adapters :as adapters]
            [microservice-boilerplate.schemas.db :as schemas.db])
  (:import [java.util UUID]))

(defn uuid-from-string
  {:malli/schema [:=> [:cat :string] :uuid]}
  [seed]
  (-> seed
      .getBytes
      UUID/nameUUIDFromBytes))

(defn uuid-from-date-amount
  {:malli/schema [:=> [:cat inst? :double] :uuid]}
  [date amount]
  (-> date
      (adapters/inst->utc-formated-string "yyyy-MM-dd hh:mm:ss")
      (str amount)
      uuid-from-string))

(defn ->wallet-transaction
  {:malli/schema [:=> [:cat inst? :double :double] schemas.db/WalletTransaction]}
  [date amount current-usd-price]
  {:wallet/id (uuid-from-date-amount date amount)
   :wallet/btc_amount amount
   :wallet/usd_amount_at (* current-usd-price amount)})

(defn can-withdrawal?
  {:malli/schema [:=> [:cat neg? pos?] :boolean]}
  [withdrawal-amount current-total]
  (-> (+ current-total withdrawal-amount)
      (>= 0)))
