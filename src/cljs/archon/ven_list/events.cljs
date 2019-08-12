(ns archon.ven-list.events
  "this namespace contains all of the events that are generated from the vendor-list view"
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.http-fx]
    [archon.config :as config]
    [ajax.core :as ajax]
    [archon.routes :as routes]
    [archon.events :as events]
    [archon.subs :as subs]))

(def get-sort-arg ;converts the name of the sorted by column in the database to the corresponding attribute in a vendor map
  {"v.updated_at" :updated-at
   "name_first" :name-first
   "name_last" :name-last
   })

(rf/reg-event-fx
  ::get-page
  ;requests new page of vendors from the backend
  (fn [world [_ token reset-vendor-list? navigate?]]
    {:db (if reset-vendor-list?
           (assoc (:db world) :vendor-list [] :last-page nil :page-index 0)
           (:db world))
     :http-xhrio {:method  :post
                  :uri     config/v-list-url
                  :params  {:token token}
                  :timeout 3000
                  :format  (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::good-page-request reset-vendor-list? navigate?]
                  :on-failure [::bad-result]}}))

(rf/reg-event-fx
  ::good-page-request
  ;adds the next page to the vendor list
  ;handles navigation to vendor-list-panel
  (fn [world [_ reset-vendor-list? navigate? {:keys [data error] :as payload}]]
    (if error
      {:db (assoc (:db world) :error error)}
      (let [{:keys [page-index city-name] :as db} (:db world)
            clean-prev-state (dissoc db :prev-state)
            {:keys [page last-page?]} data
            last-page (if last-page? ;sets value of last-page to current page index if page returned by the backend is the last
                        page-index)  ;assumes that the page index matches the newly loaded page
            vendor-page-empty? (empty? page)
            updated-db {:db (-> db
                                (update :vendor-list conj page)
                                (assoc :last-page last-page
                                       :vendor-page-empty? vendor-page-empty?))}
            updated-effects-for-nav (if (and navigate?
                                             (not vendor-page-empty?))
                                      (-> updated-db
                                          (assoc-in [:db :prev-state] clean-prev-state)
                                          (assoc :navigate (routes/name-to-url ::routes/vendor-list-panel {:city city-name})))
                                      updated-db)]
        updated-effects-for-nav))))

(rf/reg-event-fx
  ::load-new-vendor-page
  ;assembles the token in order to dispatch get-page to get new page of vendors from backend
  (fn [world [_ reset-vendor-list? navigate?]]
    (let [city-name (-> (:db world)
                        :city-name)
          {:keys [sort-by sort-order]} (-> (:db world)
                                           :vendor-list-display)
          sort-arg-key (get-sort-arg sort-by)                                                     ;uses a map to get the name of the key of the value sorted by in the vendor map
          {:keys [vendor-id] sort-arg sort-arg-key} (if-not reset-vendor-list? (-> (:db world)    ;gets the token from the last vendor requested
                                                                                   :vendor-list   ;will be nil if it's the first request
                                                                                   (last)
                                                                                   (last)))]
      (rf/dispatch [::get-page {:vendor-id vendor-id
                                :sort-by [sort-by sort-arg sort-order]
                                :filter-by [["city" city-name]]
                                :page-size 2} reset-vendor-list? navigate?]))))

(rf/reg-event-fx
  ::nav-prev-page
  ;navigates to previous page
  (fn [world _]
    {:db (update (:db world) :page-index dec)}))

(rf/reg-event-fx
  ::nav-next-page
  ;navigates to next page by increasing the page index, loads page if page unloaded
  (fn [world _]
    (let [{:keys [page-index vendor-list]} (:db world)
          page-index (inc page-index)]
      (if-not (get vendor-list page-index)
        (rf/dispatch [::load-new-vendor-page]))
      {:db (assoc (:db world) :page-index page-index)}
    )))

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
                  :on-success [::good-vendor-details-request]
                  :on-failure [::bad-result]}}))

(rf/reg-event-fx
  ::good-vendor-details-request
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

(rf/reg-event-fx
  ::sort-by-change
  (fn [world [_ new-sort-by]]
    (let [old-sort-by (-> (:db world)
                          :vendor-list-display
                          :sort-by)]
    {:db (assoc-in (:db world) [:vendor-list-display :sort-by] new-sort-by)
     :dispatch [::load-new-vendor-page true false]})))

(rf/reg-event-fx
  ::sort-order-change
  (fn [world [_ new-sort-order]]
    (let [old-sort-order (-> (:db world)
                             :vendor-list-display
                             :sort-order)]
    {:db (assoc-in (:db world) [:vendor-list-display :sort-order] new-sort-order)
     :dispatch [::load-new-vendor-page true false]})))
