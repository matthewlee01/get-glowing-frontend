(ns archon.city.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
  ::city-name-change
  (fn [db [_ city-name]]
    (assoc db :city-name city-name)))
