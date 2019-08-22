(ns archon.ven-list.events
  "this namespace contains all of the events that are generated from the vendor-list view"
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.http-fx]
    [archon.config :as config]
    [ajax.core :as ajax]
    [archon.routes :as routes]
    [archon.events :as events]
    [archon.subs :as subs]
    [archon.ven-list.css :as vl-css]
    [cljsjs.jquery]))

(def get-sort-arg ;converts the name of the sorted by column in the database to the corresponding attribute in a vendor map
  {"v.updated_at" :updated-at
   "name_first" :name-first
   "name_last" :name-last
   })

(def COST-BUFFER 5) ;the amount above min cost or below max cost when a value of min cost higher than max cost is entered or vice versa

(def SCREEN-BUFFER 30) ;the amount above the bottom of the screen needed to be scrolled to before more vendors are loaded

(def jquery (js* "$"))

(defn scrolled-near-bottom? []
  (<= (.height (jquery (js* "document")))
      (+ (.height (jquery (js* "window")))
         (.scrollTop (jquery (js* "window")))
         SCREEN-BUFFER)))

(defn calc-loaded-rows []
  "calculates the number of rows that should be loaded by the initial load"
  (inc (quot (.height (jquery (js* "window")))
          (+ vl-css/vendor-card-height
             (* vl-css/vendor-card-margin 2)))))

(defn allow-new-page-request? [db reset-vendor-list? initial-load?]
  "contains logic for if request for more vendors should be made"
  (let [{:keys [last-vendor-loaded? vendor-list]} db]
    (or reset-vendor-list?
        (and (not last-vendor-loaded?)
             (or (not initial-load?)
                 (< (count vendor-list)
                    (calc-loaded-rows)))))))

(rf/reg-event-fx
  ::load-more-vendors
    (fn [world [_ reset-vendor-list? navigate? initial-load?]]
      (if (allow-new-page-request? (:db world) reset-vendor-list? initial-load?)
        {:dispatch [::get-page reset-vendor-list? navigate? initial-load?]})))

;loads more vendors when scrolled to near bottom of page
(.scroll (jquery (js* "window"))
         (fn []
           (let [on-vendor-list-panel? (= @(rf/subscribe [::subs/active-panel]) :archon.routes/vendor-list-panel)]
             (if (and on-vendor-list-panel? (scrolled-near-bottom?)) (do (rf/dispatch [::load-more-vendors]))))))

;on initial page load, enters information about window size into the db
(.ready (jquery (js* "window"))
       (fn []
         (rf/dispatch [::update-window-size (.width (jquery (js* "window"))) (.height (jquery (js* "window")))])))

;on resizing of window, updates information in the db
(.resize (jquery (js* "window"))
         (fn []
           (rf/dispatch [::update-window-size (.width (jquery (js* "window"))) (.height (jquery (js* "window")))])))

(def max-columns (count vl-css/vendor-column-thresholds))

(defn get-vendor-col-num [width]
  "based on the information about media query thresholds in css file,
   calculates how many vendors are displayed per row"
  (loop [ind 0]
      (if (< width (get vl-css/vendor-column-thresholds ind))
        (recur (inc ind))
        (- max-columns ind))))

(rf/reg-event-fx
  ::update-window-size
  (fn [world [_ width height]]
    (let [vendor-col-num (get-vendor-col-num width)]
      {:db (assoc (:db world) :vendor-col-num vendor-col-num :window-height height :window-width width)})))

(defn assemble-filters
  [city {:keys [min-rating min-cost max-cost]}]
    (cond-> [["city" city]]
            (and min-rating
                 (not= "" min-rating)) (conj ["min-rating" min-rating])
            (and min-cost
                 (not= "" min-cost)) (conj ["min-cost" (* min-cost 100)]) ;converts from dollars to cents
            (and max-cost
                 (not= "" max-cost)) (conj ["max-cost" (* max-cost 100)]))
  )

(defn assemble-token [db reset-vendor-list?]
  ;assembles the token in order to dispatch get-page to get new page of vendors from backend
    (let [{:keys [city-name vendor-list-display vendor-col-num]} db
          {:keys [sort-by sort-order]} vendor-list-display
          filter-by (assemble-filters city-name vendor-list-display)
          sort-arg-key (get-sort-arg sort-by)                                        ;uses a map to get the name of the key of the value sorted by in the vendor map
          {:keys [vendor-id] sort-arg sort-arg-key} (if-not reset-vendor-list? (-> db    ;gets the token from the last vendor requested
                                                                                   :vendor-list   ;will be nil if it's the first request
                                                                                   (last)
                                                                                   (last)))]
      {:vendor-id vendor-id
       :sort-by [sort-by sort-arg sort-order]
       :filter-by filter-by
       :page-size vendor-col-num}))


(rf/reg-event-fx
  ::get-page
  ;requests new page of vendors from the backend
  (fn [world [_ reset-vendor-list? navigate? initial-load?]]
    (let [token (assemble-token (:db world) reset-vendor-list?)]
      (if-not (:vendor-request-flag (:db world)) ;if true, means that another request is still being processed
        {:db (if reset-vendor-list?
               (assoc (:db world) :vendor-list [] :last-page nil :page-index 0 :vendor-request-flag true)
               (assoc (:db world) :vendor-request-flag true))
         :http-xhrio {:method  :post
                      :uri     config/v-list-url
                      :params  {:token token}
                      :timeout 3000
                      :format  (ajax/json-request-format)
                      :response-format (ajax/json-response-format {:keywords? true})
                      :on-success [::good-page-request reset-vendor-list? navigate? initial-load?]
                      :on-failure [::bad-result]}}))))

(rf/reg-event-fx
  ::good-page-request
  ;adds the next page to the vendor list
  ;handles navigation to vendor-list-panel
  (fn [world [_ reset-vendor-list? navigate? initial-load? {:keys [data error] :as payload}]]
    (if error
      {:db (assoc (:db world) :error error)}
      (let [{:keys [page-index city-name] :as db} (:db world)
            db (dissoc db :vendor-request-flag)
            clean-prev-state (dissoc db :prev-state)
            {:keys [page last-page?]} data
            last-page (if last-page? ;sets value of last-page to current page index if page returned by the backend is the last
                        page-index)  ;assumes that the page index matches the newly loaded page
            vendor-page-empty? (empty? page)
            updated-db {:db (-> db
                                (update :vendor-list conj page)
                                (assoc :last-page last-page
                                       :vendor-page-empty? vendor-page-empty?
                                       :last-vendor-loaded? last-page?))}
            updated-effects-for-initial-load (if initial-load?
                                               (merge updated-db {:dispatch [::load-more-vendors false false true]})
                                               updated-db)
            updated-effects-for-nav (if (and navigate?
                                             (not vendor-page-empty?))
                                      (-> updated-effects-for-initial-load
                                          (assoc-in [:db :prev-state] clean-prev-state)
                                          (assoc :navigate (routes/name-to-url ::routes/vendor-list-panel {:city city-name})))
                                      updated-effects-for-initial-load)]
        updated-effects-for-nav))))


;(rf/reg-event-fx
;  ::nav-prev-page
;  ;navigates to previous page
;  (fn [world _]
;    {:db (update (:db world) :page-index dec)}))

;(rf/reg-event-fx
;  ::nav-next-page
;  ;navigates to next page by increasing the page index, loads page if page unloaded
;  (fn [world _]
;    (let [{:keys [page-index vendor-list]} (:db world)
;          page-index (inc page-index)]
;      (if-not (get vendor-list page-index)
;        (rf/dispatch [::load-new-vendor-page]))
;      {:db (assoc (:db world) :page-index page-index)}
;    )))

(rf/reg-event-fx
  ::request-vendor-details
  (fn [_world [_ vendor-id]]
    {:http-xhrio {:method  :post
                  :uri    config/ven-details-url 
                  :params {:vendor-id vendor-id} 
                  :timeout 3000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::good-vendor-details-request]
                  :on-failure [::bad-result]}}))

(rf/reg-event-fx
  ::good-vendor-details-request
  (fn [world [_ {:keys [data errors] :as payload}]]
    (let [db (:db world)
          ven-id (:vendor-id payload)
          url-string (routes/name-to-url ::routes/vendor-details-panel {:vendor-id ven-id})]

      {:db (assoc db :vendor-details payload)
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
     :dispatch [::load-more-vendors true false true]})))

(rf/reg-event-fx
  ::sort-order-change
  (fn [world [_ new-sort-order]]
    (let [old-sort-order (-> (:db world)
                             :vendor-list-display
                             :sort-order)]
    {:db (assoc-in (:db world) [:vendor-list-display :sort-order] new-sort-order)
     :dispatch [::load-more-vendors true false true]})))

(rf/reg-event-fx
  ::min-rating-change
  (fn [world [_ new-min-rating]]
    (let [new-min-rating (if (> new-min-rating 5)
                           5
                           (if (<= new-min-rating 0)
                             ""
                             new-min-rating))]
    {:db (assoc-in (:db world) [:vendor-list-display :min-rating] new-min-rating)
           :dispatch [::load-more-vendors true false true]})))

(rf/reg-event-fx
  ::min-cost-change
  (fn [world [_ new-min-cost]]
    (let [new-min-cost (if (< new-min-cost 0)
                         0
                         new-min-cost)]
      {:db (assoc-in (:db world) [:vendor-list-display :min-cost] new-min-cost)})))

(rf/reg-event-fx
  ::adjust-min-cost
  (fn [world _]
    (let [{:keys [min-cost max-cost]} (-> (:db world)
                                          :vendor-list-display)
          new-min-cost (if (< (js/parseInt max-cost) (js/parseInt min-cost)) ;will return false when either max or min cost is "" because parseInt will evaluate to NaN
                         (- (js/parseInt max-cost) COST-BUFFER)
                         min-cost)]
      {:db (assoc-in (:db world) [:vendor-list-display :min-cost] new-min-cost)})))

(rf/reg-event-fx
  ::max-cost-change
  (fn [world [_ new-max-cost]]
    (let [new-max-cost (if (< new-max-cost 0)
                         0
                         new-max-cost)]
      {:db (assoc-in (:db world) [:vendor-list-display :max-cost] new-max-cost)})))

(rf/reg-event-fx
  ::adjust-max-cost
  (fn [world _]
    (let [{:keys [min-cost max-cost]} (-> (:db world)
                                          :vendor-list-display)
          new-max-cost (if (< (js/parseInt max-cost) (js/parseInt min-cost))
                         (+ (js/parseInt min-cost) COST-BUFFER)
                         max-cost)]
      {:db (assoc-in (:db world) [:vendor-list-display :max-cost] new-max-cost)})))

(rf/reg-event-fx
  ::toggle-cost-filter-box
  (fn [world _]
    (let [box-hidden? (-> (:db world)
                          :cost-filter-box-hidden?)]
      (if-not box-hidden? (rf/dispatch [::load-more-vendors true false true]))
      {:db (update (:db world) :cost-filter-box-hidden? not)})
    ))
