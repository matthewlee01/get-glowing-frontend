(ns archon.ven-profile.css
  (:require [archon.common-css :as css]
            [cljss.core :refer-macros [defstyles]]))

(defstyles input-panel []
  {:background-color css/color-glow-main
   :padding "10px"})

(defstyles input-label []
  {:width "90%"
   :display "block"})

(defstyles input-container []
  {:background-color css/color-glow-main
   :margin "10px"
   :display "flex"
   :flex-direction "column"
   :align-items "flex-start"
   :justify-content "space-between"
   :width "100%"})

(defstyles profile-update-input [border bg]
  {:background-color bg
   :border-width "1px"
   :border-style "solid"
   :border-color border
   :font-size css/input-font-size
   :width "90%"})
