(ns archon.ven-details.events
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.http-fx]
    [archon.routes :as routes]
    [archon.config :refer [debug-out graphql-url]]
    [ajax.core :as ajax :refer [json-request-format 
                                json-response-format]]))

(rf/reg-event-fx
  ::request-vendor-details
  (fn [_world [_ vendor_id]]
    {:http-xhrio {:method  :post
                  :uri    graphql-url
                  :params {:query (str "query vendor_by_id($id:Int!)"
                                       "{vendor_by_id (vendor_id: $id)"
                                       "{vendor_id name_first profile_pic"
                                       " services{s_description s_duration s_name s_price s_type}}}")
                           :variables {:id vendor_id}}
                  :timeout 3000
                  :format (json-request-format)
                  :response-format (json-response-format {:keywords? true})
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
    (debug-out (str "BAD data: " payload))
    (assoc db :active-panel :services-panel)))
