(ns microservice-boilerplate.schemas.db
  (:require [schema.core :as s]
            [malli.core :as m]))

(def wallet {:wallet/id uuid?
             :wallet/btc_amount number?
             :wallet/usd_amount_at number?
             :wallet/created_at inst?})

(map #(-> % vec) (select-keys wallet [:wallet/id
                                      :wallet/btc_amount
                                      :wallet/usd_amount_at]))

(def x 
  [:map
   [:a 1]
   [:b 2]])

[:map (->> (select-keys wallet [:wallet/id :wallet/btc_amount :wallet/usd_amount_at])
           (map #(-> % vec)))]

(def WalletTransaction
  [])

; (def wallet {:wallet/id s/Uuid
;              :wallet/btc_amount s/Num
;              :wallet/usd_amount_at s/Num
;              :wallet/created_at s/Inst})

; (s/defschema WalletTransaction
;   (select-keys wallet [:wallet/id
;                        :wallet/btc_amount
;                        :wallet/usd_amount_at]))

; (s/defschema WalletEntry
;   (select-keys wallet [:wallet/id
;                        :wallet/btc_amount
;                        :wallet/usd_amount_at
;                        :wallet/created_at]))
