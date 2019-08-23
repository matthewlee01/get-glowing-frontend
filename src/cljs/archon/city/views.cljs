(ns archon.city.views
  (:require [re-frame.core :as rf]
            [archon.subs :as subs]
            [archon.city.events :as c-events]
            [archon.city.css :as css]
            [archon.routes :as routes]
            [archon.ven-list.events :as vl-events]
            [archon.common-css :as c-css]))

(defn panel []
  [:div [:div {:class (c-css/input-class)}
          (c-css/TextInputField
            {:type "text"
             :placeholder "City Name"
             :auto-focus true
             :on-key-press #(if (= 13 (.-charCode %)) ;; 13 is code for enter key
                             (rf/dispatch [::vl-events/load-more-vendors true true true]))
             :default-value @(rf/subscribe [::subs/city-name])
             :on-change #(do (rf/dispatch [::c-events/city-name-change (-> % .-target .-value)])
                             (rf/dispatch [::c-events/hide-empty-vendor-page-error]))})
          (c-css/SubmitButton {:on-click #(rf/dispatch [::vl-events/load-more-vendors true true true])};#(rf/dispatch [::vl-events/load-new-vendor-page true true])}
            "Enter")]
        [:div {:class (css/error-box)
        	      :hidden (not @(rf/subscribe [::subs/vendor-page-empty?]))}
          "Sorry, your search yielded no results in our database. Please try again!"]])
