(ns archon.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [archon.db :as db]
   [archon.config :refer [api-endpoint-url debug-out]]))

(re-frame/reg-event-db
 ::log-db
 (fn [db _]
   (cljs.pprint/pprint db)
   db))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

;; want to get rid of this, or have it only available in debug mode?
(re-frame/reg-event-db
  ::set-active-panel
  (fn [db [_ value]]
    (assoc db :active-panel value)))

(re-frame/reg-event-db
  ::good-http-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (assoc db :active-panel :vendors-panel :vendor-list (:vendor_list data))))

(re-frame/reg-event-db
  ::bad-http-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (debug-out (str "BAD data: " payload))
    (assoc db :active-panel :services-panel)))

(re-frame/reg-event-db
  ::take-me-back
  (fn [db _]
    (:prev-state db)))

(re-frame/reg-event-db 
  ::set-auth-result
  (fn [db [_ auth-result]]
    (assoc-in db [:user :auth-result] auth-result)))

(re-frame/reg-event-db 
  ::set-profile
  (fn [db [_ profile]]
    (assoc-in db [:user :profile] profile)))


