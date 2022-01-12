(ns unit.parenthesin.components.config-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [com.stuartsierra.component :as component]
            [matcher-combinators.test :refer [match?]]
            [parenthesin.components.config :as components.config]
            [parenthesin.utils :as u]))

(use-fixtures :once u/with-malli-intrumentation)

(defn- create-and-start-system!
  [{:keys [config]}]
  (component/start-system
   (component/system-map :config config)))

(deftest config-mock-component-test
  (testing "config should return mocked config"
    (let [system (create-and-start-system!
                  {:config (components.config/new-config {:webserver/port 1234
                                                          :env :test})})]

      (is (match? {:config {:webserver/port 1234
                            :env :test}}
                  (:config system))))))
