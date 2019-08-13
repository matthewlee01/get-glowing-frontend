(ns archon.ven-upload.events
  (:require
    [re-frame.core :as rf]
    [archon.events :as events]
    [archon.v-services.events :as s-events]
    [archon.routes :as routes]
    [archon.config :as config]
    [ajax.core :as ajax :refer [json-request-format
                                json-response-format
                                POST]]))

(rf/reg-event-fx
  ::show-ven-upload
  (fn [world _]
    {:dispatch [::events/navigate-to ::routes/vendor-upload-panel]}))

(rf/reg-event-fx
  ::upload-photo
  (fn [world _]
    (let [image (:file (:db world))
          service-id 5
          desc (:description (:db world))
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
    (println "payload: " payload)
    {:db (:db world)}))

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
                            :description "")}))

(rf/reg-event-fx
  ::set-description
  (fn [world [_ txt]]
    {:db (assoc (:db world) :description txt)}))
