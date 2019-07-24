(ns archon.ven-appts.css
  (:require
    [cljss.core :as css :refer-macros [defstyles]]
    [archon.common-css :as com-css]
    [cljss.reagent :refer-macros [defstyled]]))

(defstyles appt-view-box []
  {:display "flex"})

(defstyles appt-list-column []
  {:display "flex"
   :width "30%"
   :flex-direction "column"})

(defstyles appt-card []
  {:background-color com-css/color-booked
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
   :font-size "12px"
   :font-family "Arial"})
