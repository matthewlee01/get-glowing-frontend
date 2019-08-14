(ns archon.ven-details.views
  (:require
    [re-frame.core :as rf]
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]
    [archon.subs :as subs]
    [archon.config :as config]
    [archon.ven-details.events :as vde]
    [archon.ven-details.css :as vd-css]
    [archon.ven-upload.views :as vu-views]
    [archon.events :as events]
    [archon.common-css :as css]
    [archon.calendar.events :as cal-events]))

(defn make-service-card-div [service-info current-vendor-id]
  "makes a vector with a index number and the div for the service card;
   used with map-indexed to group services into columns"
  (let [{:keys [s_description s_duration s_name s_price s_type service_id]} service-info
        date (cal-events/get-current-date)]
    ^{:key service-info}
     [:div [:div {:class (vd-css/service-card-title 16)
                  :on-click #(rf/dispatch [::vde/goto-ven-calendar current-vendor-id date service_id])} s_name]
           [:div {:class (vd-css/service-card-box)}
             [:div s_type]
             [:div s_duration]
             [:div s_price]
             [:div s_description]]]))

(defn photo-thumbnail
  [photo]
  (let [{:keys [description filename service-id published]} photo]
    ^{:key filename}
    [:div {:on-click #(rf/dispatch [::events/set-selected-photo photo])}
     [:img {:class (vd-css/photo-thumbnail published)
            :src (str config/image-url filename) 
            :alt description}]]))
  
(defn photo-image
  [filename]
  [:img {:class (vd-css/photo-image)
         :src (str config/image-url filename)
         :alt "ERROR: Image not found"}])

(defn photo-modal-content
  [photo]
  [:div {:class (vd-css/photo-modal-content)}
   [:img {:src (str config/image-url (:filename photo))
          :alt "ERROR: Image not found"
          :class (vd-css/photo-image)}]
   [:div {:class (vd-css/photo-description)} (:description photo)]])
  
(defn photo-modal
  []
  (if-let [photo @(rf/subscribe [::subs/selected-photo])]
    [:div {:class (css/modal-bg)}
     [:div {:class (vd-css/photo-modal-box)}
      (if (= @(rf/subscribe [::subs/active-panel]) :archon.routes/vendor-upload-panel)
        (vu-views/photo-modal-buttons photo))
      (photo-modal-content photo)]]))

(defn photo-display
  [photos]
  [:div {:class (vd-css/photo-display)}
   (map photo-thumbnail photos)])

(defn photo-panel
  [empty-text photos]
  "constructs the panel that holds the photo thumbnails, displaying a fallback string if empty"
  (if photos
    [:div 
     [:h5 "Photo Gallery:"]
     (photo-display photos)]
    [:div
     [:h5 empty-text]]))

(defn panel []
    (let [{:keys [vendor_id 
                  name
                  name_first
                  name_last
                  summary
                  photos
                  services 
                  profile_pic]} 
          @(rf/subscribe [::subs/vendor-details])
          first-last-name (str name_first " " name_last)
          fullname (if name name first-last-name)
          date (cal-events/get-current-date)
          prof-pic (str "/" profile_pic)]    ;; TODO - this hack actually needs a fix 
                                             ;; on the server to send an absolute path
                                             ;; for now hack the path to be absolute
     [:div [vd-css/profile-img {:src prof-pic
                                :alt prof-pic}]
           [vd-css/profile-title fullname]
           [vd-css/profile-summary summary]
           [:br]
           [vd-css/service-select-label "Select a service to view availability:"]
           [:div {:class (vd-css/service-card-array)}
             (map #(make-service-card-div % vendor_id) services)]
           (photo-panel "This vendor hasn't uploaded any photos yet." photos)]))
             
            
  
