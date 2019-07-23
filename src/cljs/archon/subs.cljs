(ns archon.subs
  (:require
   [re-frame.core :as re-frame]
   [archon.routes :as routes]))

(re-frame/reg-sub
 ::app-name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
  ::active-panel
  (fn [db _]
    ;; get the matched-route obj saved in the db
    (let [route (:active-panel db)]
      ;; return the name associated with the route
      (routes/match-to-name route))))

(re-frame/reg-sub
  ::vendor-list
  (fn [db _]
    (:vendor-list db)))

(re-frame/reg-sub
  ::city-name
  (fn [db _]
    (:city-name db)))

(re-frame/reg-sub
  ::current-vendor-id
  (fn [db _]
    (:current-vendor-id db)))

(re-frame/reg-sub
  ::vendor-details
  (fn [db _]
    (:vendor-details db)))

(re-frame/reg-sub
  ::auth-result
  (fn [db _]
    (get-in db [:user :auth-result])))

(re-frame/reg-sub
  ::user-info
  (fn [db _]
    (:user-info db)))

(re-frame/reg-sub
  ::vendor-calendar
  (fn [db _]
    (:vendor-calendar db)))

(re-frame/reg-sub
  ::vendor-list-empty?
  (fn [db _]
    (:vendor-list-empty? db)))

(re-frame/reg-sub
  ::last-error-payload
  (fn [db _]
    (:last-error-payload db)))

(re-frame/reg-sub
  ::selected-service
  (fn [db _]
    (:selected-service db)))

(re-frame/reg-sub
  ::error
  (fn [db _]
    (:error db)))

(re-frame/reg-sub
  ::services-list
  (fn [db _]
    (:services-list db)))

(re-frame/reg-sub
  ::active-service
  (fn [db _]
    (:active-service db)))