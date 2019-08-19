(ns archon.ven-upload.views
  (:require [archon.common-css :as css]
            [archon.config :as config]
            [archon.events :as events]
            [re-frame.core :as rf]
            [archon.subs :as subs]
            [archon.ven-upload.css :as vu-css]
            [archon.ven-details.views :as vd-views]
            [archon.ven-upload.events :as vu-events])) 

(defn image-selector
  []
  [:input {:type "file"
           :accept "image/png, image/jpeg"
           :value @(rf/subscribe [::subs/filename])
           :on-change #(rf/dispatch [::vu-events/set-file (aget (-> % .-target .-files) 0)
                                                          (-> % .-target .-value)])}])

(defn description-input
  []
  [:input {:type "text"
           :placeholder "Add a description for this picture..."
           :value @(rf/subscribe [::subs/image-description])
           :class (vu-css/text-field-input)
           :on-change #(rf/dispatch [::vu-events/set-description (-> % .-target .-value)])}])

(defn submit-button
  []
  (css/SubmitButton {:on-click #(rf/dispatch [::vu-events/upload-image])
                     :disabled (or (empty? @(rf/subscribe [::subs/image-description]))
                                   (empty? @(rf/subscribe [::subs/filename])))} "Submit"))

(defn publish-all-button
  []
  (css/SubmitButton {:on-click #(rf/dispatch [::vu-events/update-image {:published "ALL"}])
                     :disabled @(rf/subscribe [::subs/all-published?])} "Publish All"))

(defn publish-warning
  []
  [:h4 {:hidden @(rf/subscribe [::subs/all-published?])
        :class (vu-css/publish-warning)} "Highlighted images have been uploaded, but not yet published to your public page. Click 'Publish All' to publish them all at once."])

(defn panel
  []
  (let [images @(rf/subscribe [::subs/image-list])]
    [:div 
     (vd-views/image-modal)
     [:div {:class (vu-css/upload-box)}
      (image-selector)
      (description-input)
      (submit-button)
      (publish-all-button)]
     (publish-warning)
     (vd-views/image-panel "You haven't uploaded any images yet." images)]))
     
