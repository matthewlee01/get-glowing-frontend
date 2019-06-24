(ns archon.db
  (:require [archon.routes :as routes]))

(def default-db
  {:name "get glowing!"
   :active-panel (routes/name-to-url :routes/city-panel)
   :city-name ""})
