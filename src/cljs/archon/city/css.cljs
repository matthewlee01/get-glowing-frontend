(ns archon.city.css
  (:require
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]])) 


(defstyles error-box []
  {:font-size "20px"
   :padding "5px 5px"
   :border "3px solid #FFB6C1"
  	})