(ns archon.ven-list.views
  (:require
    [archon.ven-list.events :as vle]  ;; vendor list events
    [archon.ven-list.css :as vl-css]
    [archon.events :as events]
    [archon.subs :as subs]
    [archon.routes :as routes]
    [archon.common-css :as css]
    [re-frame.core :as rf]))
     
(def MAX-RATING 5)

(defn percentage [rating]
  (->> (/ rating 5.0)
       (* 100)
       Math/floor
       int))

(defn vendor-card [vendor]
  (let [{:keys [vendor-id name-first name-last addr-city profile-pic
                services-summary rating-summary]} vendor
        {:keys [min max]} services-summary
        {:keys [count average]} rating-summary
        rating% (percentage average)
        min$ (/ min 100)
        max$ (/ max 100)
        prof_pic (str "/" profile-pic)]  ;; TODO - this hack actually needs a fix
                                         ;; on the server to send an absolute path
                                         ;; for now hack the path to be absolute
    ^{:key vendor-id}
    [:div
      (vl-css/vendor-card-div {:on-click #(rf/dispatch [::vle/request-vendor-details vendor-id])}
        (vl-css/profile-img {:src prof_pic
                             :alt prof_pic})
        [:div
          (vl-css/title (str  name-first " " name-last))
          (vl-css/price-stats (str  "$" min$ " - $" max$))
          (vl-css/empty-stars
            [:div {:class (vl-css/filled-stars rating%)}])
          [:div [:div addr-city]]])]))

(defn get-cost-button-color []
  (if (and @(rf/subscribe [::subs/cost-filter-box-hidden?])
           (= (@(rf/subscribe [::subs/vendor-list-display]) :min-cost) "")
           (= (@(rf/subscribe [::subs/vendor-list-display]) :max-cost) ""))
    vl-css/button-inactive-color
    vl-css/button-active-color))

(defn cost-filters
 []
 [:div {:class (vl-css/cost-filter-screen-container)}
   (vl-css/CostFilterButton {:on-click #(rf/dispatch [::vle/toggle-cost-filter-box]) :style {:background-color (get-cost-button-color)}} "Cost")
   (vl-css/cost-filter-screen-bg {:on-click #(rf/dispatch [::vle/toggle-cost-filter-box]) :hidden @(rf/subscribe [::subs/cost-filter-box-hidden?])})
   (vl-css/cost-filter-box {:hidden @(rf/subscribe [::subs/cost-filter-box-hidden?])}
     (vl-css/FilterLabel "Min Cost")
     (vl-css/FilterInputField {:value (@(rf/subscribe [::subs/vendor-list-display]) :min-cost) :min 0 :type "number" :on-change #(rf/dispatch [::vle/min-cost-change (-> % .-target .-value)]) :on-blur #(rf/dispatch [::vle/adjust-min-cost])})
     [:br]
     (vl-css/FilterLabel "Max Cost")
     (vl-css/FilterInputField {:value (@(rf/subscribe [::subs/vendor-list-display]) :max-cost) :min 0 :type "number" :on-change #(rf/dispatch [::vle/max-cost-change (-> % .-target .-value)]) :on-blur #(rf/dispatch [::vle/adjust-max-cost])})
   )])

(defn vendor-page
  []
  (flatten @(rf/subscribe [::subs/vendor-list]) ;@(rf/subscribe [::subs/page-index])
  ))

(defn panel []
  [:div
    [:div {:style {:display "flex" :flex-wrap "wrap"}}
      (css/SelectInput {:value (@(rf/subscribe [::subs/vendor-list-display]) :sort-by) :on-change #(rf/dispatch [::vle/sort-by-change (-> % .-target .-value)])}
        [:option {:value "v.updated_at"} "Recently Updated"]
        [:option {:value "name_first"} "First Name"]
        [:option {:value "name_last"} "Last Name"] 
      )
      (css/SelectInput {:value (@(rf/subscribe [::subs/vendor-list-display]) :sort-order) :on-change #(rf/dispatch [::vle/sort-order-change (-> % .-target .-value)])}
        [:option {:value "asc"} "Ascending"]
        [:option {:value "desc"} "Descending"]
      )
      (vl-css/FilterLabel "Min Rating")
      (vl-css/FilterInputField {:value (@(rf/subscribe [::subs/vendor-list-display]) :min-rating) :min 0 :max MAX-RATING :type "number" :on-change #(rf/dispatch [::vle/min-rating-change (-> % .-target .-value)])})
      (cost-filters)]
    [:div {:class (vl-css/vendor-card-flex)} (map vendor-card (vendor-page))]
    ;nav buttons from before, could make it toggled between scrolling and buttons in the future?
    ;(vl-css/nav-button {:on-click #(rf/dispatch [::vle/nav-prev-page])
    ;                    :hidden (= 0 @(rf/subscribe [::subs/page-index]))} "Previous Page")
    ;(vl-css/nav-button {:on-click #(rf/dispatch [::vle/nav-next-page])
    ;                    :hidden (= @(rf/subscribe [::subs/last-page]) @(rf/subscribe [::subs/page-index]))} "Next Page")
    [:br]   
    (css/BackButton {:on-click #(rf/dispatch [::events/take-me-back (routes/name-to-url ::routes/city-panel)])} "Return")])
