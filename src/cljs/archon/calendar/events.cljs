(ns archon.calendar.events
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.http-fx]
    [archon.routes :as routes]
    [archon.config :refer [debug-out graphql-url calendar-url]]
    [ajax.core :as ajax :refer [json-request-format 
                                json-response-format]]    
    [cljs-time.core :as ct-core]
    [archon.events :as events]
    [cljs-time.format :as ct-format]))

(rf/reg-event-fx
  ::update-calendar
  (fn [world [_ date]]
    (let [vendor-id (get-in world [:db :vendor-details :vendor_id])
          uri (str calendar-url "/" vendor-id "/" date)]
      {:http-xhrio {:method :get
                    :uri    uri
                    :timeout 3000
                    :response-format (json-response-format {:keywords? true})
                    :on-success [::good-result]
                    :on-failure [::bad-result]}})))

(rf/reg-event-fx
  ::good-result
  (fn [world [_ {:keys [data errors] :as payload}]]
    (let [db (:db world)]
      {:db (assoc db :vendor-calendar payload)})))

(rf/reg-event-db
  ::bad-result
  events/show-error)
 
(defn get-current-date []
  "uses cljs-time to get the current date in yyyy-mm-dd format"
  (let [current-time (ct-core/now)
        formatter (ct-format/formatter "yyyy-MM-dd")]
    (ct-format/unparse formatter current-time)))
 

