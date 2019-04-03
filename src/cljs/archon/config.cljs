(ns archon.config)

(def debug?
  ^boolean goog.DEBUG)

(defn api-endpoint-url []
  (if debug?
    "http://localhost:8888/graphql"
    "http://archon.j3mc.ca:8888/graphql"))

(defn debug-out [params]
  (when debug?
    (js/alert params)))


