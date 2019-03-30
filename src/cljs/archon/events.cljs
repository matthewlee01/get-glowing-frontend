(ns archon.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [archon.db :as db]))
   
(re-frame/reg-event-db
 ::log-db
 (fn [db _]
 	 (print db)))


(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
  ::set-active-panel
  (fn [db [_ value]]
    (assoc db :active-panel value)))

(re-frame/reg-event-fx
  ::submit-city
  (fn [_world  [_ val]]
    (js/alert (str "_world:" _world " val:" val))
    (js/alert {:http-xhrio {:method  :post}
                          :uri     "http://localhost:8888/graphql"
                          :params {:query "query vendor_list($city:String!){vendor_list (addr_city: $city) { vendor_id addr_city name_first name_last }}"
                                   :variables {:city (:city-name (:db _world))}} 
                          :timeout 3000
                          :format (ajax/json-request-format)
                          :response-format (ajax/json-response-format {:keywords? true})
                          :on-success [::good-http-result]
                          :on-failure [::bad-http-result]})
    {:http-xhrio {:method  :post
                  :uri     "http://localhost:8888/graphql"
                  :params {:query "query vendor_list($city:String!){vendor_list (addr_city: $city) { vendor_id addr_city name_first name_last }}"
                           :variables {:city (:city-name (:db _world))}} 
                  :timeout 3000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::good-http-result]
                  :on-failure [::bad-http-result]}}))

(re-frame/reg-event-fx
	::request-vendor-info
	(fn [_world [_ vendor_id]]
		{:http-xhrio {:method  :post
                  :uri     "http://localhost:8888/graphql"
                  :params {:query "query vendor_by_id($id:String!){vendor_by_id (id: $id) { vendor_id name_first services {s_description s_duration s_name s_price s_type}}}"
                           :variables {:id vendor_id}} 
                  :timeout 3000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::good-http-result]
                  :on-failure [::bad-http-result]}}))



(re-frame/reg-event-db
  ::good-http-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (js/alert (str "data: --> " data))
    (assoc db :active-panel :vendors-panel :vendor-list (:vendor_list data))))

(re-frame/reg-event-db
  ::bad-http-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (js/alert (str "BAD data: " payload))
    (assoc db :active-panel :services-panel)))

(re-frame/reg-event-db
  ::city-name-change
  (fn [db [_  city-name]]
    (assoc db :city-name city-name)))
