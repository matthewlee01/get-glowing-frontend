(ns archon.ven-upload.views
  (:require [archon.common-css :as css]
            [archon.config :as config]
            [re-frame.core :as rf]
            [archon.subs :as subs]
            [archon.ven-upload.css :as vu-css]
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

(defn photo-thumbnail
  [photo]
  (let [{:keys [description filename service-id published]} photo]
    ^{:key filename}
    [:div {:on-click #(rf/dispatch [::vu-events/set-selected-photo photo])}
     [:img {:class (vu-css/photo-thumbnail published)
            :src (str config/image-url filename) 
            :alt description}]]))
  
(defn photo-display
  [photos]
  [:div {:class (vu-css/photo-display)}
   (map photo-thumbnail photos)])

(defn photo-image
  [filename]
  [:img {:class (vu-css/photo-image)
         :src (str config/image-url filename)
         :alt "ERROR: Image not found"}])

(defn photo-modal-buttons
  [photo]
  [:div {:class (vu-css/photo-modal-buttons)}
   [:span {:class (css/modal-close)
           :on-click #(rf/dispatch [::vu-events/set-selected-photo nil])}
    (goog.string/unescapeEntities "&times")]
   (if (not (:published photo))
     (css/SubmitButton {:on-click #(rf/dispatch [::vu-events/publish-photo (:filename photo)])} "Publish"))
   (css/SubmitButton {:on-click #(rf/dispatch [::vu-events/delete-photo (:filename photo)])} "Delete")])

(defn photo-modal-content
  [photo]
  [:div {:class (vu-css/photo-modal-content)}
   [:img {:src (str config/image-url (:filename photo))
          :alt "ERROR: Image not found"
          :class (vu-css/photo-image)}]
   [:div {:class (vu-css/photo-description)} (:description photo)]])
  
(defn photo-modal
  []
  (if-let [photo @(rf/subscribe [::subs/selected-photo])]
    [:div {:class (css/modal-bg)}
     [:div {:class (vu-css/photo-modal-box)}
      (photo-modal-buttons photo)
      (photo-modal-content photo)]]))
  
(defn panel
  []
  (let [photos @(rf/subscribe [::subs/photo-list])]
    [:div 
     (photo-modal)
     [:div {:class (vu-css/upload-box)}
      (photo-selector)
      (description-input)
      (submit-button)
      (publish-all-button)]
     [:div
      (photo-display photos)]]))
     
