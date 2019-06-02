(ns archon.calendar.views
  (:require 
    [re-frame.core :as rf]
    [archon.calendar.css :as cal-css]
    [archon.subs :as subs]))

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
(def sample-calendar1 {:available [[]]
                       :booked [[]]
                       :template [[]]
                       :date "2019-07-17"})
(def sample-calendar2 {:available [[]]
                       :booked [[]]
                       :template [[]]
                       :date "2019-07-19"})

(defn time-label
  [time-int]
  [:label {:class (cal-css/side-time-label)} (minute-int-to-time-string time-int)])
  
(defn available-slot
  [[start-time end-time]]
  [:div {:class (cal-css/time-slot-class "#FFB6C1")}
   (str (minute-int-to-time-string start-time) " - Available")])
   
(defn unavailable-slot
  [[start-time end-time]]
  [:div {:class (cal-css/time-slot-class "#7a7978")}
   (str (minute-int-to-time-string start-time) " - Unavailable")])
          
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
  [available booked template time-slot]
  (if (and (or (time-within-coll? time-slot available)
               (time-within-coll? time-slot template))
           (not (time-within-coll? time-slot booked)))
    (available-slot time-slot)
    (unavailable-slot time-slot)))
  
(defn calendar-day-column
  "creates a column of time slots based on the available times"
  [vendor-calendar]
  (let [{:keys [available booked template date]} vendor-calendar]
   (->> [:div {:class (cal-css/calendar-column)}
         [date]
         (map (partial construct-time-slot available booked template) DISPLAYED_TIMES)]
        (mapcat #(if (sequential? %) % [%])) ;; flattens the elements created in the map into the parent div
        (vec))))

(defn time-label-column
  "creates a column of time labels based on the displayed times"
  []
  (->> [:div {:class (cal-css/calendar-column)}
        (map (fn [[start-time _]] (time-label start-time)) DISPLAYED_TIMES)]
       (mapcat #(if (sequential? %) % [%])) ;; flattens the elements created in the map into the parent div
       (vec)))

;;the prev day and next day cols will just hold sample data for now, need to and functionality for other days in the future
(defn panel
  []
  [:div {:class (cal-css/calendar-day-box)}
   [:div {:class (cal-css/time-label-box)} (time-label-column)]
   [:div {:class (cal-css/outer-day-box)} (calendar-day-column sample-calendar1)] 
   [:div {:class (cal-css/centre-day-box)} (calendar-day-column @(rf/subscribe [::subs/vendor-calendar]))]
   [:div {:class (cal-css/outer-day-box)} (calendar-day-column sample-calendar2)]])

