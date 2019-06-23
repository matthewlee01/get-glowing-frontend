(ns archon.common-css
  (:require
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]))

;; VARIABLE DEFS - START

(def input-font-size "18px")
(def color-glow-main "#FFB6C1")                             ;; pink
(def color-nav "#7A7978")                                   ;; grey
(def anim-timing "0.3s")

;; VARIABLE DEFS - END

;; HEADER STYLES - START

(defstyles huge-title []
           {:font-size "80px"
            :color color-glow-main
            :cursor "pointer"})

;; HEADER STYLES - END

;; LAYOUT/NAV STYLES - START
(defstyles main-nav []
           {:width "auto"})

(defstyled NavBarElement :button
           {:background-color color-nav
            :border           "1px"
            :border-radius    "4px"
            :box-sizing       "border-box"
            :color            "#FFFFFF"
            :margin           "5px"
            :padding          "2px 12px"
            :text-align       "center"
            :font-size        input-font-size
            :cursor           "pointer"
            :transition       anim-timing
            :width            "120px"
            :height           "62px"
            :vertical-align   "top"
            :&:hover          {:opacity "0.8"}})

(defstyled BackButton :button
           {
            :background-color color-nav
            :border           "1px"
            :border-radius    "4px"
            :box-sizing       "border-box"
            :color            "#FFFFFF"
            :margin           "5px"
            :padding          "2px 12px"
            :text-align       "center"
            :font-size        input-font-size
            :cursor           "pointer"
            :transition       anim-timing
            :width            "120px"
            :height           "30px"
            :vertical-align   "top"
            :&:hover          {:opacity "0.8"}})

;; LAYOUT/NAV STYLES - END

;; INPUT STYLES - START
(defstyles input-class []
           {:margin "auto"
            :width "67%"})

(defstyled InputLabel :label
           {:font-family "Arial"
            :margin "15px"
            :color "#7a7978"
            :font-size "15px"})

(defstyled TextInputField :input
           {:padding "5px"
            :margin "10px 0"
            :font-size input-font-size
            :width "67%"})

(defstyled SelectInput :select
           {
            :height "35px"
            :font-size input-font-size})


(defstyled SubmitButton :button
           {:background-color color-glow-main
            :border "1px"
            :border-radius "10px"
            :box-sizing "border-box"
            :margin "5px 0"
            :padding "4px 10px"
            :font-size "16px"
            :font-weight "bold"
            :cursor "pointer"
            :min-width "80px"
            :height "42px"
            :transition anim-timing
            :&:hover {:opacity "0.9"}})

;; INPUT STYLES - END

(defstyled label :div
  {:display "inline-block"
;   :background-color "#ABC"
   :font-size "16px"
   :font-family "Arial"
   :vertical-align "middle"
   :padding "15px 10px"})

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

