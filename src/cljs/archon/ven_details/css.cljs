(ns archon.ven-details.css
  (:require
    [cljss.core :as css :refer-macros [defstyles]]
    [archon.common-css :as com-css]
    [cljss.reagent :refer-macros [defstyled]]))


(defstyles service-card-array []
  {:display "flex"
   :flex-wrap "wrap"
   :&:after {:clear "both"}
   ::css/media {[:only :screen :and [:min-width "700px"]] {:width "700px"}          
                [:only :screen :and [:min-width "1050px"]] {:width "1050px"}}})

(defstyles service-card-title [top]
  {:position "relative"
   :cursor "pointer"
   :top (str top "px")
   :padding "10px"
   :font-size "28px"})

(defstyles service-card-box []
  {:height "90px"
   :width "290px"
   :padding "10px"
   :margin "0px 10px"
   :border "5px solid #FFB6C1"})

(defstyled profile-img :img
  {:display "block"
   :margin-left "auto"
   :margin-right "auto"
   :margin-top "10px"
   :margin-bottom "20px"
   :object-fit "cover"
   :width "400px"
   :height "300px"})

(defstyled profile-title :label
  {:display "block"
   :text-align "center"
   :font-size "28px"})

(defstyled profile-summary :div
  {:font-size "18px"
   :margin "16px 16px"})


(defstyled service-select-label :div
  {:font-size "16px"
   :margin "16px"})

(defstyles image-modal-box []
  {:width "70%"
   :height "90%"
   :display "flex"
   :flex-flow "column"
   :justify-content "flex-start"
   :background-color "#d9d9d9"
   :margin "5% auto"})

(defstyles image-modal-buttons []
  {:display "flex"
   :flex-direction "row-reverse"
   :justify-content "space-between"
   :flex-wrap "wrap"})

(defstyles image-modal-content []
  {:display "flex"
   :flex-wrap "wrap"
   :flex-grow "2"
   :flex-flow "column"
   :justify-content "space-evenly"})

(defstyles image-display []
  {:display "flex"
   :flex-wrap "wrap"
   :justify-content "flex-start"
   :padding "0px"
   :width "100%"})

(defstyles image-thumbnail [published?]
  {:display "flex"
   :object-fit "cover"
   :margin "3px"
   :height "300px"
   :border-style "dashed"
   :border-width "5px"
   :border-color (if published?
                   "white"
                   com-css/color-glow-main)                
   :width "300px"})

(defstyles image-description []
  {:padding "10px 10px"
   :margin "5px 5px"
   :background-color "white"
   :border-style "solid"
   :border-color "black"
   :border-width "1px"})

(defstyles full-image []
  {:width "90%"
   :object-fit "contain"
   :margin "0% auto"
   :max-height "75%"})


