(ns archon.ven-upload.events
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.http-fx]
    [archon.events :as events]
    [archon.v-services.events :as s-events]
    [archon.routes :as routes]
    [archon.config :as config]
    [ajax.core :as ajax :refer [json-request-format
                                json-response-format
                                POST]]))

(defn all-published?
  [photos]
  (->> (map :published photos)
       (every? true?)))

(rf/reg-event-fx
  ::publish-photo
  (fn [world [_ filename]]
    {:http-xhrio {:method  :post
                  :uri    config/v-publish-url
                  :params {:access-token (get-in world [:db :access-token])
                           :filename filename}
                  :timeout 3000
                  :format (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success [::good-photo-result]
                  :on-failure [::bad-result]}}))

(rf/reg-event-fx
  ::delete-photo
  (fn [world [_ filename]]
    {:http-xhrio {:method  :post
                  :uri    config/v-delete-photo-url
                  :params {:access-token (get-in world [:db :access-token])
                           :filename filename}
                  :timeout 3000
                  :format (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success [::good-photo-result]
                  :on-failure [::bad-result]}}))

(rf/reg-event-fx
  ::ven-get-photos
  (fn [world _]      
    {:http-xhrio {:method  :post
                  :uri    config/v-photos-url
                  :params {:access-token (get-in world [:db :access-token])}
                  :timeout 3000
                  :format (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success [::good-photo-result]
                  :on-failure [::bad-result]}}))

(rf/reg-event-fx
  ::good-photo-result
  (fn [world [_ payload]]
    (let [photos (:photos payload)]
      {:dispatch [::clear-files]
       :db (assoc (:db world) :photo-list photos
                              :all-published? (all-published? photos))})))

(rf/reg-event-fx
  ::show-ven-upload
  (fn [world _]
    (rf/dispatch [::ven-get-photos])
    {:dispatch [::events/navigate-to ::routes/vendor-upload-panel]}))

(rf/reg-event-fx
  ::upload-photo
  (fn [world _]
    (let [image (:file (:db world))
          service-id 5
          desc (if (empty? (:photo-description (:db world)))
                 "No description"
                 (:photo-description (:db world)))
          access-token (:access-token (:db world))
          form-data (doto
                      (js/FormData.)
                      (.append "image" image)
                      (.append "description" desc)
                      (.append "access-token" access-token))]
      (POST config/v-upload-url {:body form-data
                                 :response-format :json
                                 :keywords? true
                                 :handler #(rf/dispatch [::good-upload-result %])
                                 :error-handler #(rf/dispatch [::bad-result %])}))
    {:dispatch [::clear-files]}))
      
(rf/reg-event-fx 
  ::good-upload-result
  (fn [world [_ payload]]
    {:dispatch [::ven-get-photos]}))

(rf/reg-event-fx
  ::bad-result
  events/show-error)

(rf/reg-event-fx
  ::set-file
  (fn [world [_ file filename]]
    {:db (assoc (:db world) :file file
                            :filename filename)}))
    
(rf/reg-event-fx
  ::clear-files
  (fn [world _]
    {:db (assoc (:db world) :file nil
                            :filename ""
                            :selected-photo nil
                            :photo-description "")}))

(rf/reg-event-fx
  ::set-description
  (fn [world [_ txt]]
    {:db (assoc (:db world) :photo-description txt)}))
