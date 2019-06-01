(ns archon.calendar.css
  (:require 
    [cljss.core :as css :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]))

(defstyles calendar-day-box []
  {:display "flex"
   :padding "12px"
   :width "100%"})


(defstyles time-slot-class [color]
  {:background-color color
   :width "90%"
   :margin "1px"
   :padding "2px 12px"
   :text-align "center"
   :display "inline-block"
   :height "50px"
   :line-height "50px"
   :vertical-align "middle"
   :color "white"
   :box-sizing "border-box"
   :font-size "16px"
   :font-family "Arial"})

(defstyles calendar-column []
  {:width "375px"
   :height "100%"
   :vertical-align "middle"
   :margin "2px"})

(defstyles centre-day-box []
  {:display "flex"
   :height "100%"})

(defstyles outer-day-box []
  {:display "flex"
   :height "100%"
   ::css/media {[:only :screen :and [:max-width "1050px"]] {:display "none"}}})

(defstyles time-label-box []
  {:display "flex"
   :height "100%"
   :width "100px"})

(defstyles side-time-label []
  {:width "100%"
   :vertical-align "34px"
   :height "50px"
   :display "inline-block"})

  
