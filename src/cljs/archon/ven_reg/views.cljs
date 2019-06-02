(ns archon.ven-reg.views
  (:require [re-frame.core :as rf]
            [archon.ven-reg.events :as es]
            [archon.subs :as subs]
            [archon.common-css :as common-css]
            [cljss.core :as css :refer-macros [defstyles defkeyframes]]
            [cljss.reagent :refer-macros [defstyled]]))

(defstyles input-class []
  {:margin "50px"})

(defstyles field-label [font-size]
  {:font-family "Arial"
   :padding "15px 15px"
   :color "#7a7978"
   :font-size (str font-size "px")})

(defstyles input-field []
  {:padding "14px 14px"
   :font-size "18px"})

(defn panel []
  (let [{:keys [given_name family_name email] :or {given_name "" family_name "" email ""}} @(rf/subscribe [::subs/profile])]
    [:div {:class (input-class)}
      [:div
        [:label {:class (field-label 15)} "Enter your email address:"]
        [:input (merge  {:class (input-field)} 
                        {:type "text"
                         :default-value email
                         ::auto-focus true
                         ::on-load #(rf/dispatch [::es/vr-email-change email])
                         ::on-blur #(rf/dispatch [::es/vr-email-change (-> % .-target .-value)])})]]
      [:div
        [:label {:class (field-label 15)} "First Name:"]
        [:input (merge  {:class (input-field)} 
                        {:type "text"
                         :default-value given_name
                         :auto-focus true
                         :on-load #(rf/dispatch [::es/vr-first-name-change given_name])
                         :on-blur #(rf/dispatch [::es/vr-first-name-change (-> % .-target .-value)])})]]
      [:div
        [:label {:class (field-label 15)} "Last Name:"]
        [:input (merge {:class (input-field)} 
                      {:type "text"
                       :default-value family_name
                       :auto-focus true
                       :on-load #(rf/dispatch [::es/vr-last-name-change family_name])
                       :on-blur #(rf/dispatch [::es/vr-last-name-change (-> % .-target .-value)])})]]
      [:div
        [:label {:class (field-label 15)} "Address:"]
        [:input (merge  {:class (input-field)} 
                        {:type "text"
                         :auto-focus true
                         ::on-blur #(rf/dispatch [::es/vr-address-change (-> % .-target .-value)])})]]
      [:div
        [:label {:class (field-label 15)} "City:"]
        [:input (merge  {:class (input-field)} 
                        {:type "text"
                         :auto-focus true
                         :on-blur #(rf/dispatch [::es/vr-city-change (-> % .-target .-value)])})]]
      [:div
        [:label {:class (field-label 15)} "Province:"]
        [:select (merge  {:class (input-field)} 
                  {:defaultValue "British Columbia"
                   :on-change #(rf/dispatch [::es/vr-province-change (-> % .-target .-value)])})
          [:option "Alberta"]
          [:option "British Columbia"]
          [:option "Manitoba"]
          [:option "New Brunswick"]
          [:option "Newfoundland and Labrador"]
          [:option "Nova Scotia"]
          [:option "Ontario"]
          [:option "PEI"]
          [:option "Quebec"]
          [:option "Saskatchewan"]]]
      [:div
        [:label {:class (field-label 15)} "Postal Code:"]
        [:input (merge  {:class (input-field)} 
                        {:type "text"
                         :auto-focus true
                         :on-blur #(rf/dispatch [::es/vr-postal-change (-> % .-target .-value)])})]]
      [:div
        [:label {:class (field-label 15)} "Phone:"]
        [:input (merge  {:class (input-field)} 
                        {:type "text"
                         :auto-focus true
                         :on-blur #(rf/dispatch [::es/vr-phone-change (-> % .-target .-value)])})]]
   
      [common-css/submit-button {:on-click #(rf/dispatch [::es/submit-vendor-form])} "Enter"]]))

