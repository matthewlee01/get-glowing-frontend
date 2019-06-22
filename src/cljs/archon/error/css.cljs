(ns archon.error.css
  (:require
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]))

(defstyles error-message []
  {:font-size "30px"
  	})