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

(defstyled label :div
  {:display "inline-block"
;   :background-color "#ABC"
   :font-size "16px"
   :font-family "Arial"
   :vertical-align "middle"
   :padding "6px 0px"})

(defstyles avatar [sz]
  {:display "inline-block"
   :border-radius "50%"
   :width (str sz "px")
   :height (str sz "px")})

(defstyles faux-avatar [sz]
  {:display "inline-block"
;   :background-color "#CFC"
   :float "right"
   :width (str sz "px")
   :height (str sz "px")})

(defstyles header [sz]
  {:width "100%"
   :height "50px"
   :background-color "#DBC"
   :display "block"})

