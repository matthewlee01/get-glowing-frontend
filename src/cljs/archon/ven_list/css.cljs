(ns archon.ven-list.css
  (:require [cljss.core :as cljss :refer-macros [defstyles]]
            [cljss.reagent :refer-macros [defstyled]]
            [archon.common-css :as css]))

(def button-active-color "#FFB6C1")

(def button-inactive-color "#9A9998")

(defstyled vendor-card-div :div
  {:background-color "#e2c7d1"
   :display "inline-block"
   :width "300px"
   :height "340px"
   :margin "10px"
   :text-align "center"
   :box-shadow "4px 4px 8px 0 #686868"})

(defstyled profile-img :img
  {:display "block"
   :margin-left "auto"
   :margin-right "auto"
   :margin-top "10px"
   :margin-bottom "20px"
   :object-fit "cover"
   :width "260px"
   :height "200px"})

(defstyled title :p
  {:font-size "16pt"
   :font-family "Arial"
   :margin "5px"})

(defstyled price-stats :p
  {:font-size "12pt"
   :margin "0px"})

(defstyled empty-stars :div
  {:display "inline-block" :position "relative"
   :font-family "FontAwesome"
   :font-size "18px"
   :&::before {:content "\f31b \f31b \f31b \f31b \f31b"}})

(defstyles filled-stars [percent-filled]
  {:position "absolute"
   :top "0"
   :left "0"
   :white-space "nowrap"
   :overflow "hidden"
   :width (str percent-filled "%")
   :&::before {:content "\f318 \f318 \f318 \f318 \f318"
               :color "#e2246a"}})

(defstyles vendor-card-flex []
  {:display "flex"
   :flex-wrap "wrap"
   ::&:after {:clear "both"}
   ::cljss/media {[:only :screen :and [:min-width "800px"]] {:width "700px"}
                  [:only :screen :and [:min-width "1000px"]] {:width "990px"}}})

(defstyled nav-button :button
  {:background-color css/color-glow-main
   :border "1px"
   :border-radius "10px"
   :box-sizing "border-box"
   :margin "5px 5px"
   :padding "4px 10px"
   :font-size "16px"
   :font-weight "bold"
   :cursor "pointer"
   :min-width "100px"
   :height "100px"
   :transition css/anim-timing
   :&:hover {:opacity "0.9"}})

(defstyles cost-filter-screen-container []
  {:position "relative"
   :display "flex"
   :justify-content "center"
   :align-items "center"})

(defstyled CostFilterButton :button
  {:border "1px"
   :border-radius "10px"
   :box-sizing "border-box"
   :margin "10px 10px"
   :padding "10px 30px"
   :font-size "16px"
   :font-weight "bold"
   :cursor "pointer"
   :color "white"
   :z-index 2
   :&:hover {:opacity "0.9"}})

(defstyled cost-filter-screen-bg :div
  {:position "fixed"
   :bottom "0"
   :top "0"
   :right "0"
   :left "0"
   :background-color "white"
   :opacity 0.70
   :z-index 1})

(defstyled cost-filter-box :div
  {:margin "5px"
   :width "300px"
   :height "300px"
   :border-radius "5px"
   :z-index 2
   :background-color "pink"
   :position "absolute"
   :bottom "-287px"})

(defstyled FilterLabel :label
           {:font-family "Arial"
            :margin "15px"
            :color "#7a7978"
            :font-size "15px"})

(defstyled FilterInputField :input
           {:padding "5px"
            :margin "10px 0"
            :font-size css/input-font-size
            :width "60px"
            :height "40px"})