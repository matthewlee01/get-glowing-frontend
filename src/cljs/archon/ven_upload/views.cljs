(ns archon.ven-upload.views
  (:require [archon.common-css :as css]
            [archon.config :as config]
            [archon.events :as events]
            [re-frame.core :as rf]
            [archon.subs :as subs]
            [archon.ven-upload.css :as vu-css]
            [archon.ven-details.views :as vd-views]
            [archon.ven-upload.events :as vu-events])) 

(defn photo-selector
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
           :value @(rf/subscribe [::subs/photo-description])
           :class (vu-css/text-field-input)
           :on-change #(rf/dispatch [::vu-events/set-description (-> % .-target .-value)])}])

(defn submit-button
  []
  (css/SubmitButton {:on-click #(rf/dispatch [::vu-events/upload-photo])
                     :disabled (or (empty? @(rf/subscribe [::subs/photo-description]))
                                   (empty? @(rf/subscribe [::subs/filename])))} "Submit"))

(defn publish-all-button
  []
  (css/SubmitButton {:on-click #(rf/dispatch [::vu-events/publish-photo "all"])
                     :disabled @(rf/subscribe [::subs/all-published?])} "Publish All"))

(defn publish-warning
  []
  [:h4 {:hidden @(rf/subscribe [::subs/all-published?])
        :class (vu-css/publish-warning)} "Highlighted photos have been uploaded, but not yet published to your public page. Click 'Publish All' to publish them all at once."])

(defn photo-modal-buttons
  [photo]
  [:div {:class (vu-css/photo-modal-buttons)}
   [:span {:class (css/modal-close)
           :on-click #(rf/dispatch [::events/set-selected-photo nil])}
    (goog.string/unescapeEntities "&times")]
   (if (not (:published photo))
     (css/SubmitButton {:on-click #(rf/dispatch [::vu-events/publish-photo (:filename photo)])} "Publish"))
   (css/SubmitButton {:on-click #(rf/dispatch [::vu-events/delete-photo (:filename photo)])} "Delete")])

(defn panel
  []
  (let [photos @(rf/subscribe [::subs/photo-list])]
    [:div 
     (vd-views/photo-modal)
     [:div {:class (vu-css/upload-box)}
      (photo-selector)
      (description-input)
      (submit-button)
      (publish-all-button)]
     (publish-warning)
     (vd-views/photo-panel "You haven't uploaded any photos yet." photos)]))
     
