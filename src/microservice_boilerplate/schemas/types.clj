(ns microservice-boilerplate.schemas.types
  (:require [clojure.test.check.generators :as generators]
            [com.stuartsierra.component :as component]
            [malli.core :as m]
            [parenthesin.components.database :as components.database]
            [parenthesin.components.http :as components.http]
            [schema.core :as s])
  (:import [java.time LocalDateTime ZoneId]))

; (def PositiveNumber
;   (s/constrained s/Num pos? 'PositiveNumber))

; (def PositiveNumberGenerator
;   (generators/fmap bigdec (generators/double* {:infinite? false :NaN? false :min 0.0001})))

; (def NegativeNumber
;   (s/constrained s/Num neg? 'NegativeNumber))

; (def NegativeNumberGenerator
;   (generators/fmap bigdec (generators/double* {:infinite? false :NaN? false :max -0.0001})))

; (def NumberGenerator
;   (generators/fmap bigdec (generators/double* {:infinite? false :NaN? false})))

; (def TypesLeafGenerators
;   {PositiveNumber PositiveNumberGenerator
;    NegativeNumber NegativeNumberGenerator
;    s/Num NumberGenerator})

(def HttpComponent
  (m/-simple-schema
   {:type :http-component
    :pred #(satisfies? components.http/HttpProvider %)
    :type-properties {:error/message "should satisfy parenthesin.components.http/HttpProvider protocol."}}))

(def DatabaseComponent
  (m/-simple-schema
   {:type :database-component
    :pred #(satisfies? components.database/DatabaseProvider %)
    :type-properties {:error/message "should satisfy parenthesin.components.database/DatabaseProvider protocol."}}))

(def GenericComponent
  (m/-simple-schema
   {:type :generic-component
    :pred #(satisfies? component/Lifecycle %)
    :type-properties {:error/message "should satisfy com.stuartsierra.component/Lifecycle protocol."}}))

(def JavaLocalDateTime
  (m/-simple-schema
   {:type :localdatetime
    :pred #(instance? LocalDateTime %)
    :type-properties {:error/message "should be an instance of LocalDateTime."}}))

(def JavaZoneId
  (m/-simple-schema
   {:type :zone-id
    :pred #(instance? ZoneId %)
    :type-properties {:error/message "should be an instance of ZoneId."}}))

(def Components
  [:map
   [:config GenericComponent]
   [:http HttpComponent]
   [:router GenericComponent]
   [:database DatabaseComponent]])
