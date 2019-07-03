;; this namespace is meant to hide the details of the routing library we choose to use
;; to implement client side routing
(ns archon.routes
  (:require [pushy.core :as pushy]
            [reitit.core :as reitit]
            [re-frame.core :as rf]))

;; this is the client routing table data
(def client-routes [["/" ::city-panel]
                    ["/thanks" ::thanks-panel]
                    ["/vendor-list/:city" ::vendor-list-panel]
                    ["/vendor-details/:vendor-id" ::vendor-details-panel]
                    ["/calendar/:vendor-id/:date/:selected-service" ::calendar-panel]
                    ["/vendor-signup"  ::vendor-signup-panel]
                    ["/http-error" ::error-panel]])

;; this is the global router variable used by some of the following fns
(def router (reitit/router client-routes))

(def match-for (partial reitit/match-by-name router))

(defn name-to-url [& args]
  "a helper function that takes a keyword name and optional parameters
  and returns a corresponding url"
  (:path (apply match-for args)))

(defn match-to-name [match]
  "a helper function to pull the keyword name from
  a match entry from the routing table"
  (get-in match [:data :name]))

(defn match-to-url [match]
  "a helper function to pull the url path from
  a match entry from the routing table"
  (:path match))

(defn- parse-url
  "turn a URL into a data structure representing it"
  [url]
  (reitit/match-by-path router url))

(defn- dispatch-route
  "takes a data structure representing a route, and makes it happen.  used
  by pushy as a handler when the back button is used, or a browser reload."
  [matched-route]
  (rf/dispatch [:archon.events/set-active-panel matched-route]))

;; create a map to hold the above two functions
(def history
  (pushy/pushy dispatch-route parse-url))

;; initialize the pushy library
(defn install-pushstate-handlers 
  "setup pushy"
  []
  (pushy/start! history))

;; navigate to a new URL
(defn navigate-to! [url-string]
  (pushy/set-token! history url-string))

