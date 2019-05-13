(ns archon.ven-details.css
  (:require
    [cljss.core :as css :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]))


;; this style controls the way the profile image is displayed
(defstyles list-pfp [size]
  {:height (str size "px")
   :width (str size "px")})

(defstyles service-card-array []
  {:display "flex"
   :flex-wrap "wrap"
   :&:after {:clear "both"}
   ::css/media {[:only :screen :and [:min-width "700px"]] {:width "700px"}          
                [:only :screen :and [:min-width "1050px"]] {:width "1050px"}}})


(defstyles service-card-title [top]
  {:position "relative"
   :top (str top "px")
   :padding "10px"
   :font-size "28px"
   })

(defstyles service-card-box []
  {:height "90px"
   :width "290px"
   :padding "10px"
   :margin "0px 10px"
   :border "5px solid #FFB6C1"})
