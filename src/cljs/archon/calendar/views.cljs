(ns archon.calendar.views
  (:require 
    [re-frame.core :as rf]
    [archon.calendar.css :as cal-css]
    [archon.subs :as subs]
    [archon.common-css :as css]
    [archon.calendar.events :as cal-events]
    [archon.events :as events]
    [reagent.core :as r]
    [cljs-pikaday.reagent :as pikaday]))

(defn generate-displayed-times
  "generates an array of time-slots given a start time, slot count, and increment,
  ex// [0, 2, 60] would return [[0 59] [60 119]]. used for generating the
  vector for the displayed hours on each calendar day"
  [start-time slot-count increment]
  (->> (range slot-count)
       (map (fn [slot]
              (+ (* slot increment) start-time)))
       (map (fn [start]
              [start (+ start (- increment 1))]))
       (vec)))
              
(defn minute-int-to-time-string
  "takes a minute count (i.e. 90) and converts it to a time string ('1:30 AM')"
  [minute-int]
  (let [hours (quot minute-int 60)
        mins (mod minute-int 60)]
    (str
      (if (> hours 12)
        (- hours 12)
        (if (= hours 0) ;; hour 0 is a special case between 24 and 12 hour time
          12
          hours))
      ":"
      (if (= 0 mins)
        "00"
        mins)
      (if (> hours 11)
        " PM"
        " AM"))))

(def DISPLAYED_TIMES (generate-displayed-times 0 13 60)) 

(defn time-label
  [time-int]
  [:label {:class (cal-css/side-time-label)} (minute-int-to-time-string time-int)])
  
(defn available-slot
  [time-slot date]
  (let [active-panel @(rf/subscribe [::subs/active-panel])
        bookings-allowed? (not= active-panel :archon.routes/vendor-appointments-panel)]
    [:div {:class (cal-css/time-slot-class css/color-glow-main)
           :on-click #(when bookings-allowed? (rf/dispatch [::cal-events/submit-booking time-slot date]))}
     (str (minute-int-to-time-string (first time-slot)) " - Available")]))
   
(defn booked-slot
  [time-slot _]
  [:div {:class (cal-css/time-slot-class css/color-booked)}
   (str (minute-int-to-time-string (first time-slot)) " - Booked")])

(defn unavailable-slot
  [time-slot _]
  [:div {:class (cal-css/time-slot-class css/color-nav)}
   (str (minute-int-to-time-string (first time-slot)) " - Unavailable")])
          
(defn time-within-chunk?
  "checks whether a time-slot is contained within a time-chunk"
  [time-slot time-chunk]
  (and (>= (first time-slot) (first time-chunk))
       (<= (second time-slot) (second time-chunk))))
  
(defn time-within-coll?
  "checks to see if the time slot is contained anywhere within
  the collection of time-chunks"
  [time-slot time-coll]
  (->> time-coll
       (map (fn [time-chunk] (time-within-chunk? time-slot time-chunk)))
       (every? false?)        
       (not)))

(defn construct-time-slot
  [available booked template date time-slot]
  "takes some calendar data and constructs the correct component depending on the status of the time slot"
  (if (or (time-within-coll? time-slot available) ;;is this time slot available?
          (time-within-coll? time-slot template)) ;;or within the template?
    (if (time-within-coll? time-slot booked)      ;;furthermore, is it booked?
      (booked-slot time-slot date)                ;;if so, construct a booked slot
      (available-slot time-slot date))            ;;else construct an available slot
    (unavailable-slot time-slot date)))           ;;if it's not within template or available, construct an unavailable slot
  
(defn calendar-day-column
  "creates a column of time slots based on the available times"
  [vendor-calendar date]
  (let [{:keys [available booked template]} vendor-calendar]
   (->> [:div {:class (cal-css/calendar-column)}
         [date]
         (map (partial construct-time-slot available booked template date) DISPLAYED_TIMES)]
        (mapcat #(if (sequential? %) % [%])) ;; flattens the elements created in the map into the parent div
        (vec))))

(defn time-label-column
  "creates a column of time labels based on the displayed times"
  []
  (->> [:div {:class (cal-css/calendar-column)}
        (map (fn [[start-time _]] (time-label start-time)) DISPLAYED_TIMES)]
       (mapcat #(if (sequential? %) % [%])) ;; flattens the elements created in the map into the parent div
       (vec)))
    
(defn html-date-picker []
  "creates an HTML input of type 'date' to select the date for display on the calendar.
  currently doesn't work very well on desktop, as it doesn't work on Safari and bad
  input can be manually inputted"
  [:input
   {:type "date"
    :class (cal-css/html-date-picker)
    :min (cal-events/get-current-date) ;;inputting dates before this breaks everything atm, only works on mobile
    :default-value (cal-events/get-current-date)
    :on-change #(rf/dispatch [::cal-events/set-date (-> % .-target .-value)])}])

(defn pika-date-picker []
  [:div {:class (cal-css/pika-date-picker)}
   [pikaday/date-selector 
    {:date-atom (r/atom (js/Date.))
     :pikaday-attrs {:onSelect #(rf/dispatch [::cal-events/set-date
                                              (cal-events/js-date-to-string %)])}}]])
                                          
(defn date-picker []
  "a component containing both the desktop and mobile date pickers, but only one will display
  based on the media query"
  [:div
   (html-date-picker)
   (pika-date-picker)])
   
(defn panel
  []
  (let [calendar @(rf/subscribe [::subs/vendor-calendar])]
    [:div {:class (cal-css/calendar-day-box)}
     [:div {:class (cal-css/date-picker-box)}
      [:label {:class (cal-css/date-picker-label)} "Select a date:"
       (date-picker)]]
     [:div {:class (cal-css/time-label-box)} (time-label-column)]

     [:div {:class (cal-css/outer-day-box)}
      (calendar-day-column (get-in calendar [:day-before :calendar])
                           (get-in calendar [:day-before :date]))]

     [:div {:class (cal-css/centre-day-box)}
      (calendar-day-column (get-in calendar [:day-of :calendar])
                           (get-in calendar [:day-of :date]))]

     [:div {:class (cal-css/outer-day-box)}
      (calendar-day-column (get-in calendar [:day-after :calendar])
                           (get-in calendar [:day-after :date]))]]))
     

