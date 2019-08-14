(ns archon.db
  (:require [archon.routes :as routes]))

(def default-db
  {:name "get glowing!"
   :active-panel (routes/name-to-url :routes/city-panel)
   :city-name ""
   :photo-description ""
   :vendor-list []
   :page-index 0
   :vendor-list-display {:sort-by "v.updated_at" 
                         :sort-order "asc"}})
