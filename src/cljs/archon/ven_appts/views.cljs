(ns archon.ven-appts.views
  (:require
    [re-frame.core :as rf]
    [archon.subs :as subs]
    [archon.ven-appts.events :as appt-events]
    [archon.calendar.css :as cal-css]
    [archon.calendar.views :as cal-views]
    [archon.ven-appts.css :as appt-css]
    [archon.calendar.events :as cal-events]
    [cljs.reader :as cr]
    [archon.common-css :as css]))

(defn service-id-to-name
  [service-id]
  (let [services-list @(rf/subscribe [::subs/services-list])]
    (->> (map (fn [service] (if (= service-id (str (:service-id service)))
                              (:s-name service)
                              nil)) services-list)
         (some identity))))
   
(defn appointment-list-card
  [booking]
  (let [{:keys [user-id service time date booking-id]} booking]
    ^{:key booking-id}
    [:div {:class (appt-css/appt-card)
           :on-click #(rf/dispatch [::cal-events/set-date date])} 
     (str (service-id-to-name service) ", " 
          (cal-views/minute-int-to-time-string (first time)) "-"
          (cal-views/minute-int-to-time-string (last time)) ", "
          date)]))

(defn appointment-list-column
  [booking-list]
  "displays a list of bookings in a column"
  [:div {:class (appt-css/appt-list-column)}
   "Your Appointments:"
   (map appointment-list-card booking-list)])

(defn panel []
  [:div {:class (appt-css/appt-view-box)}
   (appointment-list-column @(rf/subscribe [::subs/v-bookings-list]))
   (cal-views/panel)])
