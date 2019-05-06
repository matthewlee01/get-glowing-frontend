(ns archon.ven-list.events
  (:require
    [re-frame.core :as rf]
    [archon.config :as config]
    [ajax.core :as ajax]))

(rf/reg-event-fx
  ::request-vendor-info
  (fn [_world [_ vendor_id]]
    {:http-xhrio {:method  :post
                  :uri    (config/api-endpoint-url)
                  :params {:query (str "query vendor_by_id($id:Int!)"
                                    "{vendor_by_id (vendor_id: $id)"
                                    "{vendor_id name_first profile_pic"
                                    " services{s_description s_duration s_name s_price s_type}}}")
                           :variables {:id vendor_id}}
                  :timeout 3000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::good-vendor-info-request]
                  :on-failure [::bad-http-result]}}))

(rf/reg-event-db
  ::good-vendor-info-request
  (fn [db [_ {:keys [data errors] :as payload}]]
    (assoc db :current-vendor-info (:vendor_by_id data))))
