(ns archon.views
  (:require
   [re-frame.core :as re-frame]
   [archon.subs :as subs]
   [archon.events :as events]
   [cljss.core :as css :refer-macros [defstyles defkeyframes]]
   [cljss.reagent :refer-macros [defstyled]])
  (:require-macros [cljss.core]))
   

(defn city-input []
  [:div
    [:label "City"]
    [:input {:type "text"
             :auto-focus true
             :on-blur #(re-frame/dispatch [::events/city-name-change (-> % .-target .-value)])}]
    [:button {:on-click #(re-frame/dispatch [::events/submit-city :vendors-panel])} "Enter"]])

(defn service-input []
  [:div
    [:label "Service"]
    [:input {:type "text"
             :auto-focus true}]])

(defn vendor-card [vendor]
  (let [ {:keys [vendor_id name_first name_last addr_city profile_pic]} vendor]
    [:div
     [:img {:src profile_pic
            :alt profile_pic}]
     [:p (str vendor_id " " name_first " " name_last " " addr_city)]]))

(defn vendors-panel []
  [:div
    (let [vendors @(re-frame/subscribe [::subs/vendor-list])]
      (map vendor-card vendors))])

(defstyles title [font-size]
  {:font-size (str font-size "px")
   :color "#FFC0CB"
   :margin "10px 10px"})

(defstyled bigbutton :button
  {:font-size "32px"
   :background-color (with-meta #(get {:light "white" :dark "black"} %) :color)
   :size "50px"})
  
(defn main-panel []
  [:div
    [:h1 {:class (title 80)} @(re-frame/subscribe [::subs/app-name])]
    [:div
      [:button "Become a vendor"]
      [:button "Help"]
      [:button "Login"]
      [:button "Sign Up"]]
    (condp = @(re-frame/subscribe [::subs/active-panel])
      :vendors-panel [vendors-panel]
      :city-input [city-input])])
      

