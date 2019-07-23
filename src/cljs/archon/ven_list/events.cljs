(ns archon.ven-list.events
  "this namespace contains all of the events that are generated from the vendor-list view"
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.http-fx]
    [archon.config :as config]
    [ajax.core :as ajax]
    [archon.routes :as routes]
    [archon.events :as events]))


(rf/reg-event-fx
  ::request-vendor-details
  (fn [_world [_ vendor_id]]
    {:http-xhrio {:method  :post
                  :uri    config/graphql-url
                  :params {:query (str "query vendor_by_id($id:Int!)"
                                       "{vendor_by_id (id: $id)"
                                       "{vendor_id name name_first name_last summary profile_pic"
                                       " services{s_description s_duration s_name s_price s_type service_id}}}")
                           :variables {:id vendor_id}}
                  :timeout 3000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::good-result]
                  :on-failure [::bad-result]}}))

(rf/reg-event-fx
  ::good-result
  (fn [world [_ {:keys [data errors] :as payload}]]
    (let [db (:db world)
          ven-details (:vendor_by_id data)
          ven-id (:vendor_id ven-details)
          url-string (routes/name-to-url ::routes/vendor-details-panel {:vendor-id ven-id})]

      {:db (assoc db :vendor-details ven-details)
       :navigate url-string})))

(rf/reg-event-fx
  ::bad-result
  events/show-error)
