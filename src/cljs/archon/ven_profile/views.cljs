(ns archon.ven-profile.views
  (:require [archon.subs :as subs]
            [archon.ven-details.css :as vd-css]
            [archon.ven-upload.views :as vu-views]
            [archon.ven-profile.events :as vp-events]
            [archon.ven-profile.css :as vp-css]
            [archon.config :as config]
            [archon.common-css :as css]
            [re-frame.core :as rf]))

(defn profile-update-input
  [label input-type keyname]
  (let [editable? @(rf/subscribe [::subs/editing-profile?])
        border-color (if editable? "red" css/color-glow-main)
        bg (if editable? "white" css/color-glow-main)
        profile @(rf/subscribe [::subs/vendor-profile])
        value (keyname profile)]
   [:div {:class (vp-css/input-container)}
    [:label {:class (vp-css/input-label)}
     label]
    [:input {:type input-type
             :class (vp-css/profile-update-input border-color bg)
             :value value
             :disabled (not editable?)
             :on-change #(rf/dispatch [::vp-events/update-profile-changes 
                                       keyname
                                       (-> % .-target .-value)])}]]))

(defn profile-update-edit-button []
  (if-not @(rf/subscribe [::subs/editing-profile?])
    (css/SubmitButton {:on-click #(rf/dispatch [::vp-events/enable-editing])}
                      "Edit Profile")))

(defn profile-update-submit-button []
  (if @(rf/subscribe [::subs/editing-profile?])
    (css/SubmitButton {:on-click #(rf/dispatch [::vp-events/submit-profile-changes])}
                      "Submit Changes")))

(defn profile-cancel-editing-button []
  (if @(rf/subscribe [::subs/editing-profile?])
    (css/SubmitButton {:on-click #(rf/dispatch [::vp-events/cancel-editing])}
                      "Cancel")))

(defn profile-update-buttons []
  (let [editing? @(rf/subscribe [::subs/editing-profile?])]
    (if editing?
      [:div
       [profile-cancel-editing-button]
       [profile-update-submit-button]]
      [:div
       [profile-update-edit-button]])))

(defn profile-update-panel
  []
  [:div
   [:div {:class (vp-css/input-panel)}
    [profile-update-input "First Name" "text" :name-first]
    [profile-update-input "Last Name" "text" :name-last]
    [profile-update-input "Summary" "text" :summary]
    [profile-update-input "Phone Number" "tel" :phone]
    [profile-update-input "Email" "email" :email]
    [profile-update-input "City" "text" :addr-city]
    [profile-update-input "Postal Code" "text" :addr-postal]
    [profile-update-input "Province/State" "text" :addr-state]
    [profile-update-input "Address" "text" :addr-street]]
   [profile-update-buttons]])

(defn panel
  []
  (let [profile @(rf/subscribe [::subs/vendor-profile])]
    [:div 
     [:h1 "Your Profile"]
     [vd-css/profile-img {:src (str config/image-url (:profile-pic profile))
                          :alt "Profile Picture"}]
     [:div {:class (vp-css/pfp-upload-box)}
      [:label "Upload a profile picture: "]
      [vu-views/image-selector]
      [css/SubmitButton {:on-click #(rf/dispatch [::vp-events/upload-profile-image])} "Upload"]]
     [profile-update-panel profile]]))
     
