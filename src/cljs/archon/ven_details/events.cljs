(ns archon.ven-details.events
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.http-fx]
    [archon.routes :as routes]
    [archon.config :refer [debug-out graphql-url]]
    [ajax.core :as ajax :refer [json-request-format 
                                json-response-format]]))

(rf/reg-event-fx
  ::get-calendar
  (fn [_world [_ vendor_id date]]
    (let [uri (str "http://localhost:8888/calendar/" vendor_id "/" date)]
      {:http-xhrio {:method :get
                    :uri    uri
                    :timeout 3000
                    :response-format (json-response-format {:keywords? true})
                    :on-success [::good-result]
                    :on-failure [::bad-result]}})))

(rf/reg-event-db
  ::good-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (let [ven-id (:vendor_id (:vendor-details db))
          date (get-in payload [:day-of :date])
          match (routes/url-for ::routes/calendar-panel {:vendor-id ven-id
                                                         :date date})
          url-string (:path match)]
      (routes/set-history url-string)
      (assoc db :vendor-calendar payload))))

(rf/reg-event-db
  ::bad-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (debug-out (str "BAD data: " payload))
    (assoc db :active-panel :services-panel)))

