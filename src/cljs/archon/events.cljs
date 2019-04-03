(ns archon.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [archon.db :as db]
   [archon.config :refer [api-endpoint-url debug-out]]))


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
  (fn [_world _]
    (debug-out (str "data: " (api-endpoint-url)))
    {:http-xhrio {:method  :post
                  :uri    (api-endpoint-url)
                  :params {:query "query vendor_list($city:String!){vendor_list (addr_city: $city) { vendor_id addr_city name_first name_last profile_pic}}"
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
                     :uri    (api-endpoint-url)
                     :params {:query "query vendor_by_id($id:Int!){vendor_by_id (vendor_id: $id) { vendor_id name_first services {s_description s_duration s_name s_price s_type}}}"
                              :variables {:id vendor_id}}
                     :timeout 3000
                     :format (ajax/json-request-format)
                     :response-format (ajax/json-response-format {:keywords? true})
                     :on-success [::good-vendor-info-request]
                     :on-failure [::bad-http-result]}}))

(re-frame/reg-event-db
  ::good-vendor-info-request
  (fn [db [_ {:keys [data errors] :as payload}]]
    (assoc db :current-vendor-info (:vendor_by_id data))))


(re-frame/reg-event-db
  ::good-http-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (assoc db :active-panel :vendors-panel :vendor-list (:vendor_list data))))

(re-frame/reg-event-db
  ::bad-http-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (debug-out (str "BAD data: " payload))
    (assoc db :active-panel :services-panel)))

(re-frame/reg-event-db
  ::city-name-change
  (fn [db [_  city-name]]
    (assoc db :city-name city-name)))

(re-frame/reg-event-db
  ::show-vendor-email-form
  (fn [db _]
    (assoc db :active-panel :vendor-signup-panel :prev-state db)))

(re-frame/reg-event-db
  ::vr-email-change
  (fn [db [_ vendor-email]]
    (assoc db :vr-email vendor-email)))

(re-frame/reg-event-db
  ::vr-pwd-change
  (fn [db [_ vendor-pwd]]
    (assoc db :vr-pwd vendor-pwd)))

(re-frame/reg-event-db
  ::vr-conf-pwd-change
  (fn [db [_ vendor-conf-pwd]]
    (assoc db :vr-conf-pwd vendor-conf-pwd)))

(re-frame/reg-event-db
  ::vr-first-name-change
  (fn [db [_ vendor-first-name]]
    (assoc db :vr-first-name vendor-first-name)))

(re-frame/reg-event-db
  ::vr-last-name-change
  (fn [db [_ vendor-last-name]]
    (assoc db :vr-last-name vendor-last-name)))

(re-frame/reg-event-db
  ::vr-phone-change
  (fn [db [_ vendor-phone]]
    (assoc db :vr-phone vendor-phone)))

(re-frame/reg-event-db
  ::submit-vendor-form
  (fn [db _]
;; actually what should happen here is a xml-http request, but save that for later
    (assoc db :active-panel :thanks-for-registering-panel)))

(re-frame/reg-event-db
  ::take-me-back
  (fn [db _]
    (:prev-state db)))
