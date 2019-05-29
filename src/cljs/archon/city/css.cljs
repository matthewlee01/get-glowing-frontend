(ns archon.city.css
  (:require
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]])) 

(defstyles input-class []
  {:margin "10px"})

(defstyled field-label :label
  {:font-family "Arial"
   :padding "15px 15px"
   :color "#7a7978"
   :font-size "15px"})

(defstyled input-field :input
  {:padding "14px 14px"
   :font-size "18px"
   :vertical-align "center"})

