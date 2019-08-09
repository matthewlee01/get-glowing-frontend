(ns archon.ven-list.views
  (:require
    [archon.ven-list.events :as vle]  ;; vendor list events
    [archon.ven-list.css :as vl-css]
    [archon.events :as events]
    [archon.subs :as subs]
    [archon.routes :as routes]
    [archon.common-css :as css]
    [re-frame.core :as rf]))
     

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

(defn vendor-page
  []
  (get @(rf/subscribe [::subs/vendor-list]) @(rf/subscribe [::subs/page-index])))

(defn panel []
  [:div
    (css/SelectInput {:value (@(rf/subscribe [::subs/vendor-list-display]) :sort-by) :on-change #(rf/dispatch [::vle/sort-by-change (-> % .-target .-value)])}
      [:option {:value "v.updated_at"} "Recently Updated"]
      [:option {:value "name_first"} "First Name"]
      [:option {:value "name_last"} "Last Name"] 
    )
    (css/SelectInput {:value (@(rf/subscribe [::subs/vendor-list-display]) :sort-order) :on-change #(rf/dispatch [::vle/sort-order-change (-> % .-target .-value)])}
      [:option {:value "asc"} "Ascending"]
      [:option {:value "desc"} "Descending"]
    )
    [:div {:class (vl-css/vendor-card-flex)} (map vendor-card (vendor-page))]
    (vl-css/nav-button {:on-click #(rf/dispatch [::vle/nav-prev-page])
                        :hidden (= 0 @(rf/subscribe [::subs/page-index]))} "Previous Page")
    (vl-css/nav-button {:on-click #(rf/dispatch [::vle/nav-next-page])
                        :hidden (= @(rf/subscribe [::subs/last-page]) @(rf/subscribe [::subs/page-index]))} "Next Page")
    [:br]   
    (css/BackButton {:on-click #(rf/dispatch [::events/take-me-back (routes/name-to-url ::routes/city-panel)])} "Return")])
