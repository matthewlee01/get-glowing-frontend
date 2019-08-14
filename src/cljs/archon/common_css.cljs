(ns archon.common-css
  (:require
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]))

;; VARIABLE DEFS - START

(def input-font-size "18px")
(def color-glow-main "#FFB6C1")   ;; pink
(def color-nav "#7A7978")         ;; grey
(def color-booked "#7b49b6")      ;; purple   
(def anim-timing "0.3s")

;; VARIABLE DEFS - END

;; HEADER STYLES - START

(defstyles huge-title []
           {:font-size "80px"
            :color color-glow-main
            :width "fit-content"
            :margin-top "80px"
            :margin-left "10px"
            :margin-bottom "10px"
            :cursor "pointer"})

(defstyles header [sz vendor?]
  {:width "100%"
   :height "auto"
   :position "fixed"
   :top "0px"
   :left "0px"
   :background-color (if vendor? "#CBD" "#DBC")
   :display "flex"
   :justify-content "space-between"})

(defstyles welcome-message []
  {:float "right"
   :display "flex"
   :width "auto"
   :margin-right "10px"
   :height "auto"
   :align-self "center"})

;; HEADER STYLES - END

;; LAYOUT/NAV STYLES - START
(defstyles main-nav []
           {:width "60%"})

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

(defstyles modal-bg []
  {:position "fixed"
   :z-index 1
   :left 0
   :top 0
   :width "100%"
   :height "100%"
   :background-color "rgba(0,0,0,0.4)"})

(defstyles error-modal-box []
  {:background-color "#d9d9d9"
   :margin "15% auto"
   :border "1px solid #888"
   :width "55%"})
  
(defstyles modal-close []
  {:color "#4d4e4f"
   :float "right"
   :text-align "right"
   :margin "2px 10px"
   :font-size "35px"
   :font-weight "bold"
   :&:hover {:color "black"
             :cursor "pointer"
             :text-decoration "none"}})

(defstyles error-msg []
  {:margin "14px 14px"})
