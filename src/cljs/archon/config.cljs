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

;; stores the id and domain for the auth0 client
(def auth0 
  {:client-id  "eIs2pJl4IoThsi1AdesFoagdyKktR1JB"
   :domain  "myxomatosis.auth0.com"})
