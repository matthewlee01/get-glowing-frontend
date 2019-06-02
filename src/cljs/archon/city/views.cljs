
(ns archon.city.views
  (:require [re-frame.core :as rf]
            [archon.subs :as subs]
            [archon.city.events :as c-events]
            [archon.common-css :as css]))

(defn panel []
  [:div {:class (css/input-class)}
     (css/TextInputField
       {:type "text"
        :placeholder "City Name"
        :auto-focus true
        :on-key-press #(if (= 13 (.-charCode %)) ;; 13 is code for enter key
                           (rf/dispatch [::c-events/get-vendor-list]))
        :default-value @(rf/subscribe [::subs/city-name])
        :on-change #(rf/dispatch [::c-events/city-name-change (-> % .-target .-value)])})
     (css/SubmitButton {:on-click #(rf/dispatch [::c-events/get-vendor-list])}
                  "Enter")])
