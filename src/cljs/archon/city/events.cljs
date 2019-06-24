(ns archon.city.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [archon.config :as config]
            [ajax.core :as ajax]
            [archon.routes :as routes]
            [archon.events :as events]))

(rf/reg-event-db
  ::city-name-change
  (fn [db [_ city-name]]
    (assoc db :city-name city-name)))

(rf/reg-event-db
  ::hide-empty-vendor-list-error
  (fn [db _]
    (assoc db :vendor-list-empty? false)))

(rf/reg-event-fx
  ::get-vendor-list
  (fn [_world _]
    (let [db (:db _world)
          clean-prev-state (dissoc db :prev-state)]
      {:http-xhrio {:method  :post
                    :uri    config/graphql-url
                    :params {:query (str "query vendor_list($city:String!)"
                                         "{vendor_list (addr_city: $city) "
                                         "{vendor_id addr_city name_first name_last profile_pic"
                                         " services_summary {count min max}"
                                         " rating_summary {count average}}}")
                             :variables {:city (:city-name (:db _world))}}
                    :timeout 3000
                    :format (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success [::good-http-result]
                    :on-failure [::bad-http-result]}
       :db (assoc db :prev-state clean-prev-state)})))

(rf/reg-event-fx
  ::good-http-result
  (fn [world [_ {:keys [data errors] :as payload}]]
    (let [db (:db world)
          city (:city-name db)
          vendor-list (:vendor_list data)
          vendor-list-empty? (empty? vendor-list)
          url-string (if vendor-list-empty?
                         (routes/name-to-url ::routes/city-panel)
                         (routes/name-to-url ::routes/vendor-list-panel {:city city}))]

      {:db (assoc db :vendor-list vendor-list :vendor-list-empty? vendor-list-empty?)
       :navigate url-string})))

(rf/reg-event-fx
  ::bad-http-result
  events/show-error)
