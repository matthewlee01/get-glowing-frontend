(ns archon.ven-list.events
  (:require
    [re-frame.core :as rf]
    [archon.config :as config]
    [ajax.core :as ajax]
    [archon.routes :as routes]))

(rf/reg-event-fx
  ::get-vendor-list
  (fn [_world _]
    {:http-xhrio {:method  :post
                  :uri    (config/api-endpoint-url)
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
     :db (assoc (_world :db) :prev-state (_world :db))}))

(rf/reg-event-db
  ::good-http-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (routes/set-history (str (routes/url-for :vendor-list) "/" (:city-name db)))
;;    (assoc db :active-panel :vendor-list-panel :vendor-list (:vendor_list data))
    (assoc db :vendor-list (:vendor_list data))))

(rf/reg-event-db
  ::bad-http-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (config/debug-out (str "BAD data: " payload))
    (assoc db :active-panel :services-panel)))

