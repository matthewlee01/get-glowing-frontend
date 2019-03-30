(ns archon.views
  (:require
   [re-frame.core :as re-frame]
   [archon.subs :as subs]
   [archon.events :as events]
   [cljss.core :as css :refer-macros [defstyles defkeyframes]]
   [cljss.reagent :refer-macros [defstyled]])
  (:require-macros [cljss.core]))
 
(defstyles title [font-size]
  {:font-size (str font-size "px")
   :color "#FFC0CB"
   :margin "10px 10px"})

(defstyles list-pfp [size]
  {:height (str size "px")
   :width (str size "px")})

(defstyled sexy-button :button
  {:background-color "#7a7978"
   :border "none"
   :border-radius "4px"
   :color "white"
   :margin "10px"
   :padding "15px 32px"
   :text-align "center"
   :text-decoration "none"
   :display "inline-block"
   :font-size "16px"
   :opacity "0.6"
   :cursor "pointer"
   :transition "0.3s"
   :&:hover {:opacity "1"}})

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
            :alt profile_pic
            :class (list-pfp 200)}]
     [:p (str vendor_id " " name_first " " name_last " " addr_city)]]))

(defn vendors-panel []
  [:div
    (let [vendors @(re-frame/subscribe [::subs/vendor-list])]
      (map vendor-card vendors))])

(defn main-panel []
  [:div
    [:h1 {:class (title 80)
          :on-click #(js/alert "hi")} @(re-frame/subscribe [::subs/app-name])]
    [:div
      (sexy-button "Become a vendor")
      (sexy-button "Help")
      (sexy-button "Login")
      (sexy-button "Sign Up")]
    (condp = @(re-frame/subscribe [::subs/active-panel])
      :vendors-panel [vendors-panel]
      :city-input [city-input])])
      

