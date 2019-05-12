(ns archon.city.css
  (:require
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]
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

(defstyled submit-button :button
  {:background-color "#FFB6C1"
   :border "1px"
   :border-radius "50px"
   :box-sizing "border-box"
   :color "#7a7978"
   :margin "5px"
   :padding "4px 4px"
   :text-align "center"
   :text-decoration "none"
   :display "inline-block"
   :font-size "16px"
   :cursor "pointer"
   :font-family "Arial"
   :transition "0.3s"
   :width "22%"
   :max-width "120px"
   :height "58px"
   :vertical-align "middle"
   :&:hover {:opacity "0.9"}})

