(ns archon.db
  (:require [archon.routes :as routes]))

(def default-db
  {:name "get glowing!"
   :active-panel (routes/name-to-url :routes/city-panel)
   :city-name ""
   :vendor-list []
   :page-index 0
   :cost-filter-box-hidden? true
   :vendor-list-display {:sort-by "v.updated_at" 
                         :sort-order "asc"
                         :min-rating ""
                         :min-cost ""
                         :max-cost ""}})
