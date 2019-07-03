(ns archon.ven-details.css
  (:require
    [cljss.core :as css :refer-macros [defstyles]]
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
;   :background-color "#ABC"
   :text-align "center"
   :font-size "28px"})

(defstyled profile-summary :div
  {:font-size "18px"
   :margin "16px 16px"})


(defstyled service-select-label :div
  {:font-size "16px"
   :margin "16px"})
