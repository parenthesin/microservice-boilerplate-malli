(ns parenthesin.utils
  (:require [malli.instrument :as mi]))

(defn with-malli-intrumentation
  "Wraps f ensuring there has malli collect and instrument started before running it"
  [f]
  (mi/collect!)
  (mi/instrument!)
  (f)
  (mi/unstrument!))
