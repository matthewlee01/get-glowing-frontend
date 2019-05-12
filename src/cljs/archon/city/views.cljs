
(ns archon.city.views
  (:require [re-frame.core :as rf]
            [archon.subs :as subs]
            [archon.events :as events]
            [archon.city.events :as c-events]
            [archon.city.css :as css]
            [archon.ven-list.events :as vl-events]))

(defn panel []
  [:div {:class (css/input-class)}
     [css/field-label "What city are you interested in:"]
     (css/input-field 
       {:type "text"
        :auto-focus true
        :on-key-press #(if (= 13 (.-charCode %)) ;; 13 is code for enter key
                           (rf/dispatch [::vl-events/get-vendor-list]))
        :default-value @(rf/subscribe [::subs/city-name])
        :on-change #(rf/dispatch [::c-events/city-name-change (-> % .-target .-value)])})
     (css/submit-button {:on-click #(rf/dispatch [::vl-events/get-vendor-list])}
                  "Enter")])
