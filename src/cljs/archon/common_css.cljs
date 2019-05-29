
(ns archon.common-css
  (:require
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]))
  
(defstyled nav-button :button
  {:background-color "#7a7978"
   :border "1px"
   :border-radius "4px"
   :box-sizing "border-box"
   :color "white"
   :margin "5px"
   :padding "2px 12px"
   :text-align "center"
   :text-decoration "none"
   :display "inline-block"
   :font-size "16px"
   :cursor "pointer"
   :font-family "Arial"
   :transition "0.3s"
   :width "22%"
   :max-width "120px"
   :height "62px"
   :vertical-align "top"
   :&:hover {:opacity "0.8"}})

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
   :&:hover {:opacity "0.8"}})

