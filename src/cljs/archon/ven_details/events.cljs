(ns archon.ven-details.events
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.http-fx]
    [archon.routes :as routes]
    [archon.events :as events]
    [archon.config :refer [debug-out graphql-url calendar-url]]
    [ajax.core :as ajax :refer [json-request-format 
                                json-response-format]]))

(rf/reg-event-fx
  ::get-calendar
  (fn [_world [_ vendor_id date]]
    (let [uri (str calendar-url "/" vendor_id "/" date)]
      {:http-xhrio {:method :get
                    :uri    uri
                    :timeout 3000
                    :response-format (json-response-format {:keywords? true})
                    :on-success [::good-result]
                    :on-failure [::bad-result]}})))

(rf/reg-event-fx
  ::good-result
  (fn [world [_ {:keys [data errors] :as payload}]]
    (let [db (:db world)
          ven-id (:vendor_id (:vendor-details db))
          date (get-in payload [:day-of :date])
          url-string (routes/name-to-url ::routes/calendar-panel {:vendor-id ven-id
                                                                  :date date})]
      {:db (assoc db :vendor-calendar payload)
       :navigate url-string})))

(rf/reg-event-fx
  ::bad-result
  events/show-error)

