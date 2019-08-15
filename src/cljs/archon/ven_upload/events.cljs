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
  [images]
  (->> (map :published images)
       (every? true?)))

(rf/reg-event-fx
  ::update-image
  (fn [world [_ image]]
    {:http-xhrio {:method  :post
                  :uri    config/v-images-url
                  :params {:access-token (get-in world [:db :access-token])
                           :image image}
                  :timeout 3000
                  :format (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success [::good-image-result]
                  :on-failure [::bad-result]}}))

(rf/reg-event-fx
  ::ven-get-images
  (fn [world _]      
    {:http-xhrio {:method  :post
                  :uri    config/v-image-list-url
                  :params {:access-token (get-in world [:db :access-token])}
                  :timeout 3000
                  :format (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success [::good-image-result]
                  :on-failure [::bad-result]}}))

(rf/reg-event-fx
  ::good-image-result
  (fn [world [_ payload]]
    (let [images (:images payload)]
      {:dispatch [::clear-files]
       :db (assoc (:db world) :image-list images
                              :all-published? (all-published? images))})))

(rf/reg-event-fx
  ::show-ven-upload
  (fn [world _]
    (rf/dispatch [::ven-get-images])
    {:dispatch [::events/navigate-to ::routes/vendor-upload-panel]}))

(rf/reg-event-fx
  ::upload-image
  (fn [world _]
    (let [image (:file (:db world))
          service-id 5
          desc (if (empty? (:image-description (:db world)))
                 "No description"
                 (:image-description (:db world)))
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
    {:dispatch [::ven-get-images]}))

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
                            :selected-image nil
                            :image-description "")}))

(rf/reg-event-fx
  ::set-description
  (fn [world [_ txt]]
    {:db (assoc (:db world) :image-description txt)}))
