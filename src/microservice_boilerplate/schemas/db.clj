(ns microservice-boilerplate.schemas.db
  (:require [malli.util :as mu]))

(def wallet
  [:map
   [:wallet/id :uuid]
   [:wallet/btc_amount [number? {:gen/schema [:double {:gen/NaN? false :gen/infinite? false}]}]]
   [:wallet/usd_amount_at [number? {:gen/schema [:double {:gen/NaN? false :gen/infinite? false}]}]]
   [:wallet/created_at inst?]])

(def WalletTransaction
  (mu/select-keys wallet [:wallet/id
                          :wallet/btc_amount
                          :wallet/usd_amount_at]))

(def WalletEntry
  (mu/select-keys wallet [:wallet/id
                          :wallet/btc_amount
                          :wallet/usd_amount_at
                          :wallet/created_at]))
