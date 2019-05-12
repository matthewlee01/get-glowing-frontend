(ns archon.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [day8.re-frame.http-fx]
   [archon.events :as events]
   [archon.views :as views]
   [archon.config :as config]
   [archon.routes :as routes]))
   


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)

  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/install-pushstate-handlers)
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/set-active-panel ::routes/city-panel])
  (dev-setup)
  (mount-root))

(defn log-db []
  "prints the db"
  (re-frame/dispatch [::events/log-db]))
