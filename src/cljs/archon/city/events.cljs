(ns archon.city.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [archon.config :as config]
            [ajax.core :as ajax]
            [archon.routes :as routes]
            [archon.events :as events]
            [archon.subs :as subs]
            [archon.ven-list.events :as vl-events]))

(rf/reg-event-db
  ::city-name-change
  (fn [db [_ city-name]]
    (assoc db :city-name city-name)))

(rf/reg-event-db
  ::hide-empty-vendor-page-error
  (fn [db _]
    (assoc db :vendor-page-empty? false)))

      

