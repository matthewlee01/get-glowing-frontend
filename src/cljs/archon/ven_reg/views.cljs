(ns archon.ven-reg.views
  (:require [re-frame.core :as rf]
            [archon.ven-reg.events :as es]
            [cljss.core :as css :refer-macros [defstyles defkeyframes]]
            [cljss.reagent :refer-macros [defstyled]]))

(defstyles input-class []
  {:margin "50px"})

(defstyles field-label [font-size]
  {:font-family "Arial"
   :padding "15px 15px"
   :color "#7a7978"
   :font-size (str font-size "px")})

(defstyled input-field :input
  {:padding "14px 14px"
   :font-size "18px"})

(defn panel []
  [:div {:class (input-class)}
    [:div
      [:label {:class (field-label 15)} "Enter your email address:"]
      (input-field{:type "text"
                   :auto-focus true
                   :on-blur #(rf/dispatch [::es/vr-email-change (-> % .-target .-value)])})]
    [:div
      [:label {:class (field-label 15)} "Enter your password:"] 
      (input-field{:type "text"
                   :auto-focus true
                   :on-blur #(rf/dispatch [::es/vr-pwd-change (-> % .-target .-value)])})]
    [:div
      [:label {:class (field-label 15)} "Confirm your password:"]
      (input-field{:type "text"
                   :auto-focus true
                   :on-blur #(rf/dispatch [::es/vr-conf-pwd-change (-> % .-target .-value)])})]
    [:div
      [:label {:class (field-label 15)} "First Name:"]
      (input-field{:type "text"
                   :auto-focus true
                   :on-blur #(rf/dispatch [::es/vr-first-name-change (-> % .-target .-value)])})]
    [:div
      [:label {:class (field-label 15)} "Last Name:"]
      (input-field{:type "text"
                   :auto-focus true
                   :on-blur #(rf/dispatch [::es/vr-last-name-change (-> % .-target .-value)])})]
    [:div
      [:label {:class (field-label 15)} "Phone:"]
      (input-field{:type "text"
                   :auto-focus true
                   :on-blur #(rf/dispatch [::es/vr-phone-change (-> % .-target .-value)])})]
 
    [:button {:on-click #(rf/dispatch [::es/submit-vendor-form])} "Enter"]])

