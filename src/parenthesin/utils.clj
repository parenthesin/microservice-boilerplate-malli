(ns parenthesin.utils
  (:require [malli.dev.pretty :as pretty]
            [malli.instrument :as mi]))

(defn with-malli-intrumentation
  "Wraps f ensuring there has malli collect and instrument started before running it"
  [f]
  (mi/collect! {:ns (all-ns)})
  (with-out-str (mi/instrument! {:report (pretty/thrower)}))
  (f)
  (with-out-str (mi/unstrument!)))
