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
  ::vendor-page-empty?
  (fn [db _]
    (:vendor-page-empty? db)))

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
  ::services-map
  (fn [db _]
    (:services-map db)))

(re-frame/reg-sub
  ::active-service-id
  (fn [db _]
    (:active-service-id db)))

(re-frame/reg-sub
  ::v-bookings-list
  (fn [db _]
    (:v-bookings-list db)))

(re-frame/reg-sub
  ::last-page
  (fn [db _]
    (:last-page db)))

(re-frame/reg-sub
  ::page-index
  (fn [db _]
    (:page-index db)))

(re-frame/reg-sub
  ::vendor-list-display
  (fn [db _]
    (:vendor-list-display db)))

(re-frame/reg-sub
  ::filename
  (fn [db _]
    (:filename db)))

(re-frame/reg-sub
  ::image-list
  (fn [db _]
    (:image-list db)))

(re-frame/reg-sub
  ::image-description
  (fn [db _]
    (:image-description db)))

(re-frame/reg-sub
  ::all-published?
  (fn [db _]
    (:all-published? db)))

(re-frame/reg-sub
  ::selected-image
  (fn [db _]
    (:selected-image db)))

(re-frame/reg-sub
  ::cost-filter-box-hidden?
  (fn [db _]
    (:cost-filter-box-hidden? db)))
