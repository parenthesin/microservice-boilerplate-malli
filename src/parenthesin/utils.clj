(ns parenthesin.utils
  (:require [malli.dev.pretty :as pretty]
            [malli.instrument :as mi]))

(defn with-malli-intrumentation
  "Wraps f ensuring there has malli collect and instrument started before running it"
  [f]
  (mi/collect! {:ns (all-ns)})
  (mi/instrument! {:report (pretty/reporter)})
  (f)
  (mi/unstrument!))
