(ns microservice-boilerplate.schemas.wire-out)

(def RateFloat
  [:map
   [:rate_float number?]])

(def USD
  [:map
   [:USD RateFloat]])

(def CoinDeskResponse
  [:map
   [:bpi USD]])
