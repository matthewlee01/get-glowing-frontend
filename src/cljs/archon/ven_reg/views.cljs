(ns archon.ven-reg.views
  (:require [re-frame.core :as rf]
            [archon.common-css :as css]
            [archon.subs :as subs]
            [archon.ven-reg.events :as es]
            [cljss.reagent :refer-macros [defstyled]]))

(def STATES ["Alberta" "British Columbia" "Manitoba" "New Brunswick" "Newfoundland and Labrador" "Northwest Territories" "Nova Scotia" "Nunavut" "Ontario" "PEI" "Quebec" "Saskatchewan" "Yukon"])

(defn create-state-select-option [state]
  [:option {:style {:color "black"}} state])

(defn panel []
  (let [{:keys [name-first name-last email] :or {name-first "" name-last "" email ""}} @(rf/subscribe [::subs/user-info])]
    [:div {:class (css/input-class)}
     [:h1 "Vendor Registration Form"]
     [:div
      (css/TextInputField{:type "text"
                          :default-value email
                          :placeholder "Email address"
                          :auto-focus true
                          :on-load #(rf/dispatch [::es/vr-email-change email])
                          :on-blur #(rf/dispatch [::es/vr-email-change (-> % .-target .-value)])})]
     [:div
      (css/TextInputField{:type "text"
                          :default-value name-first
                          :placeholder "First name"
                          :auto-focus true
                          :on-load #(rf/dispatch [::es/vr-first-name-change name-first])
                          :on-blur #(rf/dispatch [::es/vr-first-name-change (-> % .-target .-value)])})]
     [:div
      (css/TextInputField{:type "text"
                          :default-value name-last
                          :placeholder "Last name"
                          :auto-focus true
                          :on-load #(rf/dispatch [::es/vr-last-name-change name-last])
                          :on-blur #(rf/dispatch [::es/vr-last-name-change (-> % .-target .-value)])})]
     [:div
      (css/TextInputField{:type "text"
                          :address "Address"
                          :placeholder "Street Address"
                          :auto-focus true
                          :on-blur #(rf/dispatch [::es/vr-address-change (-> % .-target .-value)])})]
     [:div
      (css/TextInputField{:type "text"
                          :placeholder "City"
                          :auto-focus true
                          :on-blur #(rf/dispatch [::es/vr-city-change (-> % .-target .-value)])})]
     [:div
      (css/SelectInput{:required true
                       :default-value ""
                       :on-change #(rf/dispatch [::es/vr-state-change (-> % .-target .-value)])}
                     [:option {:value "" :hidden true :disabled true} "Province"]
                     (map create-state-select-option STATES))]



     [:div
      (css/TextInputField{:type "text"
                          :placeholder "Postal Code"
                          :auto-focus true
                          :on-blur #(rf/dispatch [::es/vr-postal-change (-> % .-target .-value)])})]
     [:div
      (css/TextInputField{:type "text"
                          :placeholder "Phone number"
                          :auto-focus true
                          :on-blur #(rf/dispatch [::es/vr-phone-change (-> % .-target .-value js/parseInt)])})]

     [css/SubmitButton {:on-click #(rf/dispatch [::es/submit-vendor-registration])} "Enter"]]))

