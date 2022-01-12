(ns integration.parenthesin.webserver-test
  (:require [clojure.test :refer [use-fixtures]]
            [integration.parenthesin.util :as util]
            [integration.parenthesin.util.webserver :as util.webserver]
            [parenthesin.utils :as u]
            [state-flow.api :refer [defflow]]
            [state-flow.assertions.matcher-combinators :refer [match?]]
            [state-flow.core :as state-flow :refer [flow]]))

(use-fixtures :once u/with-malli-intrumentation)

(def test-routes
  [["/plus"
    {:get {:summary "plus with spec query parameters"
           :parameters {:query [:map
                                [:x :int]
                                [:y :int]]}
           :responses {200 {:body [:map [:total :int]]}}
           :handler (fn [{{{:keys [x y]} :query} :parameters}]
                      {:status 200
                       :body {:total (+ x y)}})}
     :post {:summary "plus with spec body parameters"
            :parameters {:body [:map
                                [:x :int]
                                [:y :int]]}
            :responses {200 {:body [:map [:total :int]]}}
            :handler (fn [{{{:keys [x y]} :body} :parameters}]
                       {:status 200
                        :body {:total (+ x y)}})}}]])

(defflow
  flow-integration-webserver-test
  {:init (partial util/start-system! test-routes)
   :cleanup util/stop-system!
   :fail-fast? true}
  (flow "should interact test-routes"
    (flow "should sum the get params x & y via get"
      (match? {:status 200
               :body {:total 7}}
              (util.webserver/request! {:method  :get
                                        :uri     (str "/plus?x=" 3 "&y=" 4)})))
    (flow "should sum the body x & y via post"
      (match? {:status 200
               :body {:total 7}}
              (util.webserver/request! {:method  :post
                                        :uri     "/plus"
                                        :body    {:x 4
                                                  :y 3}})))))
