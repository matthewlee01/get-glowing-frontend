(ns archon.ven-appts.events
  (:require
    [re-frame.core :as rf]
    [archon.events :as events]
    [archon.v-services.events :as s-events]
    [archon.routes :as routes]
    [archon.config :as config]
    [ajax.core :as ajax :refer [json-request-format
                                json-response-format]]
    [archon.ven-list.events :as vl-events]
    [archon.calendar.events :as cal-events]))

(rf/reg-event-fx
  ::show-ven-appts
  (fn [world _]
    (rf/dispatch [::get-v-calendar (cal-events/get-current-date)])
    (rf/dispatch [::get-v-bookings])
    (rf/dispatch [::s-events/get-v-services-list])
    {:dispatch [::events/navigate-to ::routes/vendor-appointments-panel]}))

(rf/reg-event-fx
  ::get-v-calendar
  (fn [world [_ date]]
    {:http-xhrio {:method :post
                  :uri config/v-calendar-url
                  :params {:access-token (get-in world [:db :access-token])
                           :date date}
                  :timeout 3000
                  :format (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success [::good-cal-result]
                  :on-failure [::bad-http-result]}}))

(rf/reg-event-fx
  ::good-cal-result
  (fn [world [_ {:keys [data errors] :as payload}]]
    (let [db (:db world)]
      {:db (-> (assoc-in db [:vendor-details :vendor_id] (:vendor-id payload))
               (assoc :vendor-calendar payload))})))

(rf/reg-event-fx
  ::get-v-bookings
  (fn [world _]
    {:http-xhrio {:method :post
                  :uri config/v-bookings-url
                  :params {:access-token (get-in world [:db :access-token])}
                  :timeout 3000
                  :format (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success [::good-bookings-result]
                  :on-failure [::bad-http-result]}}))

(rf/reg-event-fx
  ::good-bookings-result
  (fn [world [_ {:keys [data errors] :as payload}]]
    (let [db (:db world)]
      {:db (assoc db :v-bookings-list payload)})))

(rf/reg-event-fx
  ::bad-http-result
  events/show-error)

