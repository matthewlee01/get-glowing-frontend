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

(defstyles photo-modal-buttons []
  {:display "flex"
   :flex-direction "row-reverse"
   :justify-content "space-between"
   :flex-wrap "wrap"})

(defstyles publish-warning []
  {:border-width "5px"
   :border-style "dashed"
   :padding "10px"
   :border-color css/color-glow-main})
