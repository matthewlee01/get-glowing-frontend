(ns archon.ven-details.views
  (:require
    [re-frame.core :as rf]
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]
    [archon.subs :as subs]
    [archon.config :as config]
    [archon.ven-details.events :as vde]
    [archon.ven-details.css :as vd-css]
    [archon.events :as events]
    [archon.ven-upload.events :as vu-events]
    [archon.common-css :as css]
    [archon.calendar.events :as cal-events]))

(defn make-service-card-div [service-info current-vendor-id]
  "makes a vector with a index number and the div for the service card;
   used with map-indexed to group services into columns"
  (let [{:keys [s-description s-duration s-name s-price s-type service-id]} service-info
        date (cal-events/get-current-date)]
    ^{:key service-info}
     [:div [:div {:class (vd-css/service-card-title 16)
                  :on-click #(rf/dispatch [::vde/goto-ven-calendar current-vendor-id date service-id])} s-name]
           [:div {:class (vd-css/service-card-box)}
             [:div s-type]
             [:div s-duration]
             [:div s-price]
             [:div s-description]]]))

(defn image-thumbnail
  [image]
  (let [{:keys [description filename service-id published]} image]
    ^{:key filename}
    [:div {:on-click #(rf/dispatch [::events/set-selected-image image])}
     [:img {:class (vd-css/image-thumbnail published)
            :src (str config/image-url filename) 
            :alt description}]
     (if (not published)
       "UNPUBLISHED")]))

(defn publish-image-button
  [image]
  (let [published-image (assoc image :published true)]
    (css/SubmitButton {:on-click #(rf/dispatch [::vu-events/update-image published-image])} "Publish")))

(defn delete-image-button
  [image]
  (let [deleted-image (assoc image :deleted true)]
    (css/SubmitButton {:on-click #(rf/dispatch [::vu-events/update-image deleted-image])} "Delete")))

(defn image-modal-buttons
  [image]
  [:div {:class (vd-css/image-modal-buttons)}
   [:span {:class (css/modal-close)
           :on-click #(rf/dispatch [::events/set-selected-image nil])}
    (goog.string/unescapeEntities "&times")
    (if (= @(rf/subscribe [::subs/active-panel]) :archon.routes/vendor-upload-panel)   
     [:div
      (if (not (:published image))
        (publish-image-button image))
      (delete-image-button image)])]])

(defn image-modal-content
  [image]
  [:div {:class (vd-css/image-modal-content)}
   [:img {:src (str config/image-url (:filename image))
          :alt "ERROR: Image not found"
          :class (vd-css/full-image)}]
   [:div
    [:div {:class (vd-css/image-description)} (str "Uploaded on " (:upload-date image))]
    [:div {:class (vd-css/image-description)} (:description image)]]])
  
(defn image-modal
  []
  (if-let [image @(rf/subscribe [::subs/selected-image])]
    [:div {:class (css/modal-bg)}
     [:div {:class (vd-css/image-modal-box)}
      (image-modal-buttons image)
      (image-modal-content image)]]))

(defn image-display
  [images]
  [:div {:class (vd-css/image-display)}
   (map image-thumbnail images)])

(defn image-panel
  [empty-text images]
  "constructs the panel that holds the image thumbnails, displaying a fallback string if empty"
  (if (not-empty images)
    [:div 
     [:h5 "Photo Gallery:"]
     (image-display images)]
    [:div
     [:h5 empty-text]]))

(defn panel []
    (let [{:keys [vendor-id 
                  name
                  name-first
                  name-last
                  summary
                  images
                  services 
                  profile-pic]} 
          @(rf/subscribe [::subs/vendor-details])
          first-last-name (str name-first " " name-last)
          fullname (if name name first-last-name)
          date (cal-events/get-current-date)
          prof-pic (str config/image-url profile-pic)]
     [:div (image-modal)
           [vd-css/profile-img {:src prof-pic
                                :alt prof-pic}]
           [vd-css/profile-title fullname]
           [vd-css/profile-summary summary]
           [:br]
           [vd-css/service-select-label "Select a service to view availability:"]
           [:div {:class (vd-css/service-card-array)}
             (map #(make-service-card-div % vendor-id) services)]
           (image-panel "This vendor hasn't uploaded any images yet." images)]))
             
            
  
