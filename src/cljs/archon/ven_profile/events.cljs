(ns archon.ven-profile.events
  (:require [re-frame.core :as rf]
            [archon.events :as events]
            [archon.ven-upload.events :as vu-events]
            [archon.config :as config]
            [ajax.core :as ajax :refer [json-request-format 
                                        json-response-format]]  
            [archon.routes :as routes])) 

(rf/reg-event-fx
  ::show-ven-profile
  (fn [world _]
    (rf/dispatch [::get-ven-profile])
    {:dispatch [::events/navigate-to ::routes/vendor-profile-panel]}))

(rf/reg-event-fx
  ::get-ven-profile
  (fn [world _]
    (let [access-token (get-in world [:db :access-token])]
      {:http-xhrio {:method :post
                    :uri config/v-profile-details-url
                    :params {:access-token access-token}
                    :timeout 3000
                    :format (json-request-format)
                    :response-format (json-response-format {:keywords? true})
                    :on-success [::good-ven-result]
                    :on-failure [::bad-http-result]}})))

(rf/reg-event-fx
  ::good-ven-result
  (fn [world [_ payload]]
    {:db (assoc (:db world) :vendor-profile payload)}))

(rf/reg-event-fx
  ::bad-http-result
  events/show-error)

(rf/reg-event-fx
  ::upload-profile-image
  (fn [world _]
    {:dispatch [::vu-events/upload-image ::good-upload-result]}))

(rf/reg-event-fx
  ::good-upload-result
  (fn [world [_ payload]]
    {:dispatch [::v-update-profile {:profile-pic (:filename payload)}]}))

(rf/reg-event-fx
  ::v-update-profile
  (fn [world [_ updated-vals]]
    (let [access-token (get-in world [:db :access-token])]
      {:http-xhrio {:method :post
                    :uri config/v-profile-url
                    :params {:access-token access-token
                             :updated-vals updated-vals}
                    :timeout 3000
                    :format (json-request-format)
                    :response-format (json-response-format {:keywords? true})
                    :on-success [::good-update-result]
                    :on-failure [::bad-http-result]}})))

(rf/reg-event-fx
  ::good-update-result
  (fn [world [_ payload]]
    {:db (assoc (:db world) :vendor-profile payload)}))

(rf/reg-event-db
  ::update-profile-changes
  (fn [db [_ keyname value]]
    (assoc-in db [:vendor-profile keyname] value)))

(rf/reg-event-db
  ::enable-editing
  (fn [db _]
    (assoc db :editing-profile? true
              :prev-profile (:vendor-profile db))))

(rf/reg-event-db
  ::cancel-editing
  (fn [db _]
    (assoc db :editing-profile? false
              :vendor-profile (:prev-profile db))))

(rf/reg-event-fx
  ::submit-profile-changes
  (fn [world _]
    (let [updated-vendor (get-in world [:db :vendor-profile])]
      {:dispatch [::v-update-profile updated-vendor]
       :db (assoc (:db world) :editing-profile? false)})))
