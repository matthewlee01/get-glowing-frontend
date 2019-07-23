(ns archon.v-services.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [archon.config :as config]
            [ajax.core :refer [json-request-format json-response-format]]
            [archon.routes :as routes]
            [archon.events :as events]))


(rf/reg-event-fx
  ::get-v-services-list
  (fn [world _]
      {:http-xhrio {:method  :post
                    :uri    config/v-services-url
                    :params {:access-token (get-in world [:db :access-token])}
                    :timeout 3000
                    :format (json-request-format)
                    :response-format (json-response-format {:keywords? true})
                    :on-success [::good-http-result]
                    :on-failure [::bad-http-result]}}))

(rf/reg-event-fx
  ::good-http-result
  (fn [world [_ {:keys [data errors] :as payload}]]
    (let [db (:db world)]
      {:db (assoc db :services-list payload)
       :dispatch [::events/navigate-to ::routes/v-services-panel]})))

(rf/reg-event-fx
  ::bad-http-result
  events/show-error)

(rf/reg-event-db
  ::name-change
  (fn [db [_ service-name]]
    (assoc-in db [:active-service :s-name] service-name)))

(rf/reg-event-db
  ::description-change
  (fn [db [_ description]]
    (assoc-in db [:active-service :s-description] description)))

(rf/reg-event-db
  ::price-change
  (fn [db [_ price]]
    (assoc-in db [:active-service :s-price] price)))

(rf/reg-event-db
  ::duration-change
  (fn [db [_ duration]]
    (assoc-in db [:active-service :s-duration] duration)))

(rf/reg-event-db
  ::type-change
  (fn [db [_ type]]
    (assoc-in db [:active-service :s-type] type)))

(rf/reg-event-fx
  ::submit-service
  (fn [world _]
    (let [db (:db world)
          service (:active-service db)]
      {:http-xhrio {:method  :post
                    :uri    config/edit-service-url
                    :params {:service service
                             :access-token (:access-token (:db world))}
                    :timeout 3000
                    :format (json-request-format)
                    :response-format (json-response-format {:keywords? true})
                    :on-success [::good-result]
                    :on-failure [::bad-result]}})))
(rf/reg-event-fx
  ::good-result
  (fn [_  _]
    {:dispatch [::get-v-services-list]}))

(rf/reg-event-fx
  ::bad-result
  events/show-error)

(rf/reg-event-db
  ::edit-service-selected
  (fn [db [_ service]]
    (assoc-in db [:active-service] service)))

