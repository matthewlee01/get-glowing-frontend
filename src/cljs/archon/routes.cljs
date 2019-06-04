;; this namespace is meant to hide the details of the routing library we choose to use
;; to implement client side routing
(ns archon.routes
  (:require [pushy.core :as pushy]
            [reitit.core :as reitit]
            [re-frame.core :as rf]
            [archon.events :as events]
            [archon.config :as config]))

;; this is the client routing table data
(def client-routes [["/" ::city-panel]
                    ["/thanks" ::thanks-panel]
                    ["/vendor-list/:city" ::vendor-list-panel]
                    ["/vendor-details/:vendor-id" ::vendor-details-panel]
                    ["/calendar/:vendor-id/:date" ::calendar-panel]
                    ["/vendor-signup"  ::vendor-signup-panel]])

;; this is the global router variable used by some of the following fns
(def router (reitit/router client-routes))

;; utility function to lookup a URL by name
(def url-for (partial reitit/match-by-name router))

(defn- parse-url
  "turn a URL into a data structure representing it"
  [url]
  (reitit/match-by-path router url))

(defn- dispatch-route
  "takes a data structure representing a route, and makes it happen.  used
  by pushy as a handler when the back button is used, or a browser reload."
  [matched-route]
  (let [panel-name (get-in matched-route [:data :name])]
    (rf/dispatch [::events/set-active-panel panel-name])))

;; create a map to hold the above two functions
(def history
  (pushy/pushy dispatch-route parse-url))

;; initialize the pushy library
(defn install-pushstate-handlers 
  "setup pushy"
  []
  (pushy/start! history))

;; navigate to a new URL
(defn set-history [url-string] 
  (pushy/set-token! history url-string))

