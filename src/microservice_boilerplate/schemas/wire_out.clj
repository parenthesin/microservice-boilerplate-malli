(ns microservice-boilerplate.schemas.wire-out)

(def TickerInfo
  [:map
   [:c [:vector number?]]])

(def XXBTZUSD
  [:map
   [:XXBTZUSD TickerInfo]])

(def KrakenResponse
  [:map
   [:result XXBTZUSD]])
