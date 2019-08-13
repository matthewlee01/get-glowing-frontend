(ns archon.ven-upload.views
  (:require [archon.common-css :as css]
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
           :class (vu-css/text-field-input)
           :on-blur #(rf/dispatch [::vu-events/set-description (-> % .-target .-value)])}])

(defn submit-button
  []
  [:button {:on-click #(rf/dispatch [::vu-events/upload-photo])} "Submit"])

(defn panel
  []
  (let [txt "congrats matthew, you have arrived at the ven-upload panel"]
    [:div
     (photo-selector)
     (description-input)
     (submit-button)]))
     
