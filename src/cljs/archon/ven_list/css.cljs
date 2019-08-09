(ns archon.ven-list.css
  (:require [cljss.core :as cljss :refer-macros [defstyles]]
            [cljss.reagent :refer-macros [defstyled]]
            [archon.common-css :as css]))

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