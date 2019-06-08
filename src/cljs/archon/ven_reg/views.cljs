(ns archon.ven-reg.views
  (:require [re-frame.core :as rf]
            [archon.common-css :as css]
            [archon.subs :as subs]
            [archon.ven-reg.events :as es]
            [cljss.reagent :refer-macros [defstyled]]))

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
      (css/SelectInput{:value "British Columbia"
                       :on-change #(rf/dispatch [::es/vr-state-change (-> % .-target .-value)])}
                     [:option "Alberta"]
                     [:option "British Columbia"]
                     [:option "Manitoba"]
                     [:option "New Brunswick"]
                     [:option "Newfoundland and Labrador"]
                     [:option "Nova Scotia"]
                     [:option "Ontario"]
                     [:option "PEI"]
                     [:option "Quebec"]
                     [:option "Saskatchewan"])]



     [:div
      (css/TextInputField{:type "text"
                          :placeholder "Postal Code"
                          :auto-focus true
                          :on-blur #(rf/dispatch [::es/vr-postal-change (-> % .-target .-value)])})]
     [:div
      (css/TextInputField{:type "text"
                          :placeholder "Phone number"
                          :auto-focus true
                          :on-blur #(rf/dispatch [::es/vr-phone-change (-> % .-target .-value)])})]

     [css/SubmitButton {:on-click #(rf/dispatch [::es/submit-vendor-registration])} "Enter"]]))

