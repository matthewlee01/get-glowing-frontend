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
  "retrieves a service's name using its service-id"
  [service-id]
  (let [services-list @(rf/subscribe [::subs/services-list])]
    (->> (map (fn [service] (if (= service-id (str (:service-id service)))
                              (:s-name service)
                              nil)) services-list)
         (some identity))))
   
(defn group-booking
  "takes a map of grouped bookings and slots the new one in"
  [grouped-bookings booking]
  (let [date (:date booking)]
    (->> [booking]
         (concat (get grouped-bookings date))
         (assoc grouped-bookings date))))

(defn group-booking-coll
  [booking-list]
  "takes a list of bookings and converts it into a map of collections of bookings, with each entry in the map representing a day"
  (reduce group-booking {} booking-list))

(defn appointment-list-card
  "a component representing a booking, navigates calendar to its date when clicked"
  [booking]
  (let [{:keys [user-id service time date booking-id]} booking]
    ^{:key booking-id}
    [:div {:class (appt-css/appt-card)
           :on-click #(rf/dispatch [::cal-events/set-date date])} 
     (str (service-id-to-name service) ", " 
          (cal-views/minute-int-to-time-string (first time)) "-"
          (cal-views/minute-int-to-time-string (last time)) ", "
          date)]))

(defn appointment-list-day
  "a component containing all the appointment cards for 1 day"
  [[date booking-list]]
  ^{:key date}
  [:div date
   (map appointment-list-card booking-list)])

(defn appointment-list-column
  "displays a list of bookings in a column, sorted by days"
  [booking-list]
  [:div {:class (appt-css/appt-list-column)}
   "Your Appointments:"
   (->> (group-booking-coll booking-list)
        (map appointment-list-day))])

(defn panel []
  [:div {:class (appt-css/appt-view-box)}
   (appointment-list-column @(rf/subscribe [::subs/v-bookings-list]))
   (cal-views/panel)])
