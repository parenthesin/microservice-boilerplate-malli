(ns integration.parenthesin.system-test
  (:require [clojure.test :refer [use-fixtures]]
            [integration.parenthesin.util :as aux]
            [integration.parenthesin.util.database :as util.database]
            [integration.parenthesin.util.http :as util.http]
            [integration.parenthesin.util.webserver :as util.webserver]
            [parenthesin.components.database :as components.database]
            [parenthesin.components.http :as components.http]
            [parenthesin.utils :as u]
            [state-flow.api :refer [defflow]]
            [state-flow.assertions.matcher-combinators :refer [match?]]
            [state-flow.core :as state-flow :refer [flow]]))

(use-fixtures :once u/with-malli-intrumentation)

(defn do-deposit!
  [{{{:keys [btc]} :body} :parameters
    {:keys [http database]} :components}]
  (let [response (components.http/request http {:url "http://coinbase.org" :method :get})
        rate (get-in response [:body :rate])
        price (* rate btc)]
    (components.database/execute database [(str "insert into wallet(price) values('" price "')")])
    {:status 201
     :body {:usd price}}))

(defn get-wallet
  [{{:keys [database]} :components}]
  (let [wallet (components.database/execute database ["select * from wallet"])]
    {:status 200
     :body (map (fn [{:wallet/keys [id price]}]
                  {:id id
                   :amount price})
                wallet)}))

(def test-routes
  [["/wallet"
    {:swagger {:tags ["wallet"]}}

    ["/deposit"
     {:post {:summary "deposit btc and return value in usd"
             :parameters {:body [:map [:btc :double]]}
             :responses {201 {:body [:map [:usd :double]]}}
             :handler do-deposit!}}]

    ["/list"
     {:get {:summary "list deposits in wallet"
            :responses {200 {:body [:vector [:map [:id :int] [:amount decimal?]]]}}
            :handler get-wallet}}]]])

(defflow
  flow-integration-system-test
  {:init (partial aux/start-system! test-routes)
   :cleanup aux/stop-system!
   :fail-fast? true}
  (flow "should interact with system"

    (flow "prepare system with http-out mocks and creating tables"
      (util.http/set-http-out-responses! {"http://coinbase.org" {:body {:rate 35000.0M}
                                                                 :status 200}})

      (util.database/execute! ["create table if not exists wallet (
                                  id serial primary key,
                                  price decimal)"])

      (flow "should insert deposit into wallet"
        (match? {:status 201
                 :body {:usd 70000.0}}
                (util.webserver/request! {:method :post
                                          :uri    "/wallet/deposit"
                                          :body   {:btc 2M}})))

      (flow "should list wallet deposits"
        (match? {:status 200
                 :body [{:id 1
                         :amount 70000.0}]}
                (util.webserver/request! {:method :get
                                          :uri    "/wallet/list"}))))))
