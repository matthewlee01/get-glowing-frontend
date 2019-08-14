(ns archon.ven-upload.css
  (:require [archon.common-css :as css]
            [cljss.core :refer-macros [defstyles]]))

(defstyles upload-box []
  {:display "flex"
   :width "100%"
   :background-color "#CBD"
   :justify-content "space-around"
   :align-items "center"
   :height "20vh"})

(defstyles text-field-input []
  {:width "40%"
   :height "30%"})

(defstyles photo-modal-box []
  {:width "70%"
   :height "90%"
   :display "flex"
   :flex-flow "column"
   :justify-content "flex-start"
   :background-color "#d9d9d9"
   :margin "5% auto"})

(defstyles photo-modal-content []
  {:display "flex"
   :flex-wrap "wrap"
   :flex-grow "2"
   :flex-flow "column"
   :justify-content "space-evenly"})

(defstyles photo-display []
  {:display "flex"
   :flex-wrap "wrap"
   :justify-content "flex-start"
   :padding "0px"
   :width "100%"})

(defstyles photo-thumbnail [published?]
  {:display "flex"
   :object-fit "cover"
   :margin "3px"
   :height "300px"
   :border-style "dashed"
   :border-width "5px"
   :border-color (if published?
                   "white"
                   css/color-glow-main)                
   :width "300px"})

(defstyles photo-description []
  {:padding "10px 10px"
   :margin "5px 5px"
   :background-color "white"
   :border-style "solid"
   :border-color "black"
   :border-width "1px"})

(defstyles photo-image []
  {:width "90%"
   :object-fit "contain"
   :margin "0% auto"
   :max-height "75%"})

(defstyles photo-modal-buttons []
  {:display "flex"
   :flex-direction "row-reverse"
   :justify-content "space-between"
   :flex-wrap "wrap"})
