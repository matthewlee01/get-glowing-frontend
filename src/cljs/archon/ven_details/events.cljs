(ns archon.ven-details.events
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.http-fx]
    [archon.routes :as routes]
    [archon.calendar.events :as cal-events]
    [archon.events :as events]
    [archon.config :refer [debug-out graphql-url calendar-url]]
    [ajax.core :as ajax :refer [json-request-format 
                                json-response-format]]))

(rf/reg-event-fx
  ::goto-ven-calendar
  (fn [_world [_ vendor_id date selected-service]]
    (rf/dispatch [::set-service selected-service])
    (let [url-string (routes/name-to-url ::routes/calendar-panel {:vendor-id vendor_id
                                                                  :date date
                                                                  :selected-service selected-service})]
      {:dispatch [::cal-events/get-calendar date]
       :navigate url-string})))

(rf/reg-event-db
  ::set-service
  (fn [db [_ service-id]]
    (assoc db :selected-service service-id)))
