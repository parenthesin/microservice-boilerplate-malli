(ns user
  (:require [malli.dev :as dev]
            [malli.dev.pretty :as pretty]))

(defn start
  []
  (dev/start! {:report (pretty/thrower)}))

(defn stop
  []
  (dev/stop!))

(comment
  (start)
  (stop))
