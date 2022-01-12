(ns microservice-boilerplate.db
  (:require [honey.sql :as sql]
            [honey.sql.helpers :as sql.helpers]
            [microservice-boilerplate.schemas.db :as schemas.db]
            [microservice-boilerplate.schemas.types :as schemas.types]
            [parenthesin.components.database :as components.database]))

(defn insert-wallet-transaction
  {:malli/schema [:=> [:cat schemas.db/WalletTransaction schemas.types/DatabaseComponent] :any]}
  [transaction db]
  (->> (-> (sql.helpers/insert-into :wallet)
           (sql.helpers/values [transaction])
           (sql.helpers/returning :*)
           sql/format)
       (components.database/execute db)
       first))

(defn get-wallet-all-transactions
  {:malli/schema [:=> [:cat schemas.types/DatabaseComponent] [:vector schemas.db/WalletEntry]]}
  [db]
  (components.database/execute
   db
   (-> (sql.helpers/select :id :btc_amount :usd_amount_at :created_at)
       (sql.helpers/from :wallet)
       sql/format)))

(defn get-wallet-total
  {:malli/schema [:=> [:cat schemas.types/DatabaseComponent] number?]}
  [db]
  (->> (-> (sql.helpers/select :%sum.btc_amount)
           (sql.helpers/from :wallet)
           sql/format)
       (components.database/execute db)
       first
       :sum))
