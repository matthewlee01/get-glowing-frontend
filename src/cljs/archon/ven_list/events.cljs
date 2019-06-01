(ns archon.ven-list.events
  "this namespace contains all of the events that are generated from the vendor-list view"
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.http-fx]
    [archon.config :as config]
    [ajax.core :as ajax]
    [archon.routes :as routes]))


(rf/reg-event-fx
  ::request-vendor-details
  (fn [_world [_ vendor_id]]
    {:http-xhrio {:method  :post
                  :uri    config/graphql-url
                  :params {:query (str "query vendor_by_id($id:Int!)"
                                       "{vendor_by_id (id: $id)"
                                       "{vendor_id name_first profile_pic"
                                       " services{s_description s_duration s_name s_price s_type}}}")
                           :variables {:id vendor_id}}
                  :timeout 3000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::good-result]
                  :on-failure [::bad-result]}}))

(rf/reg-event-db
  ::good-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (let [ven-details (:vendor_by_id data)
          ven-id (:vendor_id ven-details)
          match (routes/url-for ::routes/vendor-details-panel {:vendor-id ven-id})
          url-string (:path match)]
      ;; set the new URL so that the view is updated
      (routes/set-history url-string)
      (assoc db :vendor-details ven-details))))

(rf/reg-event-db
  ::bad-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (config/debug-out (str "BAD data: " payload))
    (assoc db :active-panel :services-panel)))
