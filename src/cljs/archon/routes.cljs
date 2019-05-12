(ns archon.routes
  (:require [pushy.core :as pushy]
            [bidi.bidi :as bidi]
            [re-frame.core :as rf]
            [archon.events :as events]
            [archon.config :as config]))

(def client-routes ["/" {""            :city-input
                         "vendor-list" :vendor-list}])
  
(defn- parse-url 
  "turn a URL into a data structure representing it"
  [url]
  (bidi/match-route client-routes url))

(defn- dispatch-route
  "takes a data structure representing a route, and makes it happen"
  [matched-route]
  (let [panel-name (keyword (str (name (:handler matched-route)) "-panel"))]
    (rf/dispatch [::events/set-active-panel panel-name])))

(def history
  (pushy/pushy dispatch-route parse-url))

(defn install-pushstate-handlers 
  "setup pushy"
  []
  (pushy/start! history))
  
(def url-for (partial bidi/path-for client-routes))
(defn set-history [url-string] 
  (pushy/set-token! history url-string))

