(ns archon.v-services.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [archon.config :as config]
            [ajax.core :refer [json-request-format json-response-format]]
            [archon.routes :as routes]
            [archon.events :as events]))

(rf/reg-event-fx
  ::show-v-services
  (fn [world _]
    (rf/dispatch [::get-v-services-list])
    {:dispatch [::events/navigate-to ::routes/v-services-panel]}))

(rf/reg-event-fx
  ::get-v-services-list
  (fn [world _]
      {:http-xhrio {:method  :post
                    :uri     config/v-services-url
                    :params {:access-token (get-in world [:db :access-token])}
                    :timeout 3000
                    :format (json-request-format)
                    :response-format (json-response-format {:keywords? true})
                    :on-success [::good-http-result]
                    :on-failure [::bad-http-result]}}))

(defn convert-to-KVP [hmap key]
  "takes a map and the key you're interested in and returns a new map with this value as
  the key and the map as the value.  ie: if node is {:foo 10 :bar 20} and you call this function 
  '(convert-to-KVP node :foo)'  the result will be {10 {:foo 10 :bar 20}}'"
  (hash-map (get hmap key) hmap))


(defn coll-to-map [coll key]
  "takes a collection of objects (maps) and returns a map that enables associative lookups 
  for each object based on the key value specified"
  (reduce #(into %1 (convert-to-KVP %2 key)) (sorted-map) coll))

(rf/reg-event-fx
  ::good-http-result
  (fn [world [_ {:keys [data errors] :as payload}]]
    (let [db (:db world)
          services-map (coll-to-map payload :service-id)]
      {:db (assoc db :services-map services-map)
       :dispatch [::events/navigate-to ::routes/v-services-panel]})))

(rf/reg-event-fx
  ::bad-http-result
  events/show-error)

(rf/reg-event-db
  ::name-change
  (fn [db [_ service-id service-name]]
    (let [services-map (:services-map db)
          new-services-map (assoc-in services-map [service-id :s-name] service-name)]
      (assoc db :services-map new-services-map))))

(rf/reg-event-db
  ::description-change
  (fn [db [_ service-id description]]
    (let [services-map (:services-map db)
          new-services-map (assoc-in services-map [service-id :s-description] description)]
      (assoc db :services-map new-services-map))))

(rf/reg-event-db
  ::price-change
  (fn [db [_ service-id price-in-dollars]]
    (let [services-map (:services-map db)
          price-in-cents (* 100 price-in-dollars)
          new-services-map (assoc-in services-map [service-id :s-price] price-in-cents)]
      (assoc db :services-map new-services-map))))


(rf/reg-event-db
  ::duration-change
  (fn [db [_ service-id duration]]
    (let [services-map (:services-map db)
          intDuration (js/parseInt duration)
          new-services-map (assoc-in services-map [service-id :s-duration] intDuration)]
      (assoc db :services-map new-services-map))))

(rf/reg-event-db
  ::type-change
  (fn [db [_ service-id type]]
    (let [services-map (:services-map db)
          new-services-map (assoc-in services-map [service-id :s-type] type)]
      (assoc db :services-map new-services-map))))

(rf/reg-event-fx
  ::submit-selected
  (fn [world _]
    "post the updated fields for the currently selected service to the backend"
    (let [db (:db world)
          services-map (:services-map db)
          active-service-id (:active-service-id db)
          service (get services-map active-service-id)]
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
  (fn [world [_ {:keys [data errors] :as payload}]]
    {:dispatch [::get-v-services-list]
     :db (-> (:db world)
             (dissoc :active-service-id)
             (assoc :error errors))}))

(rf/reg-event-fx
  ::bad-result
  events/show-error)

(rf/reg-event-db
  ::edit-selected
  (fn [db [_ service-id]]
    (assoc db :active-service-id service-id)))

(rf/reg-event-db
  ::add-new-selected
  (fn [db _]
    (let [new-services-map (into (:services-map db) {0 {:vendor-id 0 
                                                        :s-name ""
                                                        :s-description ""
                                                        :s-type ""
                                                        :s-price 0
                                                        :s-duration 0
                                                        :service-id 0}})]
      (-> db
        (assoc :active-service-id 0)  ;; this is a bogus id to denote a new service
        (assoc :services-map new-services-map))))) 

(rf/reg-event-db
  ::cancel-selected
  (fn [db _]
    (let [services-map (:services-map db)
          new-services-map (if (= 0 (:active-service-id db))
                             (dissoc services-map 0)
                             services-map)]
      (-> db
          (dissoc :active-service-id)
          (assoc :services-map new-services-map)))))


