(ns archon.calendar.events
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.http-fx]
    [archon.routes :as routes]
    [archon.config :refer [debug-out graphql-url calendar-url booking-url]]
    [ajax.core :as ajax :refer [json-request-format 
                                json-response-format]]    
    [cljs-time.core :as ct-core]
    [archon.events :as events]
    [cljs-time.format :as ct-format]))

(rf/reg-event-fx
  ::get-calendar
  (fn [world [_ date]]
    (let [vendor-id (get-in world [:db :vendor-details :vendor-id])
          uri (str calendar-url "/" vendor-id "/" date)]
      {:http-xhrio {:method :get
                    :uri    uri
                    :timeout 3000
                    :response-format (json-response-format {:keywords? true})
                    :on-success [::good-calendar-result]
                    :on-failure [::bad-result]}})))

(rf/reg-event-fx
  ::good-calendar-result
  (fn [world [_ {:keys [data errors] :as payload}]]
    (let [db (:db world)]
      {:db (assoc db :vendor-calendar payload)})))

(rf/reg-event-fx
  ::submit-booking
  (fn [world [_ time-slot date]]
    (let [db (:db world)
          user-id (get-in db [:user-info :user-id])
          vendor-id (get-in db [:vendor-details :vendor-id])
          service-id (:selected-service db) 
          booking  {:user-id user-id
                    :vendor-id vendor-id
                    :service service-id
                    :time time-slot
                    :date date
                    :access-token (:access-token db)}]
      {:http-xhrio {:method :post
                    :uri booking-url
                    :params booking
                    :timeout 3000
                    :format (json-request-format)
                    :response-format (json-response-format {:keywords? true})
                    :on-success [::good-booking-result]
                    :on-failure [::bad-result]}})))

(rf/reg-event-fx
  ::good-booking-result
  (fn [world [_ {:keys [data errors] :as payload}]]
    {:db (assoc (:db world) :error (:error payload))
     :dispatch [::get-calendar (get-in world [:db :date])]}))

(rf/reg-event-fx
  ::bad-result
  events/show-error)

(rf/reg-event-fx
  ::set-date
  (fn [world [_ date]]
    {:db (assoc (:db world) :date date)
     :dispatch [::get-calendar date]}))

(defn get-current-date []
  "uses cljs-time to get the current date in yyyy-mm-dd format"
  (let [current-time (ct-core/now)
        formatter (ct-format/formatter "yyyy-MM-dd")]
    (ct-format/unparse formatter current-time)))
 
(defn js-date-to-string
  [js-date]
  "takes a js date object and converts it to a yyyy-mm-dd string"
  (-> (.toISOString js-date)
      (.split "T")
      (get 0)))

