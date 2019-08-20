(ns archon.v-services.views
  (:require [re-frame.core :as rf]
            [archon.common-css :as css]
            [archon.subs :as subs]
            [archon.v-services.events :as vse]
            [cljss.reagent :refer-macros [defstyled]]
            [cljss.core :as cljss :refer-macros [defstyles]]))

(def card-edit-color "#FEB0B0")
(def field-edit-color "#FFFFFF")
(def field-edit-border "#FF0000")
(def debug-color "#EA3A0A")

(defstyles flex-container []
           {:display "flex"
            :justify-content "space-between"
            :flex-wrap "wrap"
            ::&:after {:clear "both"}
            ::cljss/media {[:only :screen :and [:min-width "800px"]] {:width "700px"}
                           [:only :screen :and [:min-width "1000px"]] {:width "990px"}}})

(defstyles service-card-div [bg-color]
           ;;           {:background-color "#efa2bf"
           {:background-color bg-color 
            :display "inline-block"
            :width "600px"
            :height "330px"
            :margin "10px"
            :text-align "left"
            :box-shadow "4px 4px 8px 0 #686868"})

(defstyled CardButton :button
           {:background-color css/color-card-background
            :border "1px"
            :border-radius "10px"
            :border-color "#FFFFFF"
            :box-sizing "border-box"
            :margin "5px 5px"
            :padding "4px 10px"
            :font-size "16px"
            :font-weight "bold"
            :cursor "pointer"
            :min-width "80px"
            :height "42px"
            :transition "0.3s"
            :&:hover {:opacity "0.5"}})

(defstyles BottomMenu [bg-color]
          {:background-color bg-color 
           :text-align "right"
           :margin "3px 10px"})

(defn input-field [label editable? default-value type edit-event service-id]
  (let [border (if editable? field-edit-border css/color-card-background)
        background (if editable? field-edit-color  css/color-card-background)]
    [:div
      [css/InputLabel label]
      [:input {:class (css/CardInputField border background)
               :disabled (not editable?)
               :type type
               :default-value default-value
               :auto-focus true
               :on-change (when editable?
                            #(rf/dispatch [edit-event service-id (-> % .-target .-value)]))}]]))

(defn service-card [service-vector currently-active-id]
  (let [service-id (first service-vector)
        service (second service-vector)
        {:keys [s-description s-duration s-name s-price s-type]} service
        editable? (= service-id currently-active-id)
        bg-color (if editable? 
                    card-edit-color
                    css/color-card-background)]
    ^{:key service-id}
    [:div {:class  (service-card-div bg-color)}
        [input-field "Name:" editable? s-name "text" ::vse/name-change service-id]
        [input-field "Type:" editable? s-type "text" ::vse/type-change service-id]
        [input-field "Duration:" editable? s-duration "number" ::vse/duration-change service-id]
        [input-field "Price:" editable? (/ s-price 100) "number" ::vse/price-change service-id]
        [input-field "Description:" editable? s-description "text" ::vse/description-change service-id]
        [:div {:class (BottomMenu bg-color)}
          (if editable?
            [:div
              [CardButton {:on-click #(rf/dispatch [::vse/submit-selected])} "Submit"]
              [CardButton {:on-click #(rf/dispatch [::vse/cancel-selected])} "Cancel"]]
            [:div
              [CardButton {:on-click #(rf/dispatch [::vse/edit-selected service-id])}
               "edit"]
              [CardButton "activate"]
              [css/SubmitButton "delete"]])]]))

(defn panel []
  (let [services-map @(rf/subscribe [::subs/services-map])
        active-service-id @(rf/subscribe [::subs/active-service-id])]
    [:div 
     [css/SubmitButton {:on-click #(rf/dispatch [::vse/add-new-selected])} "Add new service"]
     [:div [:h1 "Services"]]
     [:div {:class (flex-container)}
      (if (= services-map {})
          [:div {:style {:background-color "#DFDFAA"
                         :display "block"}} 
             [:h2 "No Services Currently Defined"]]
          [:div
            (map #(service-card % active-service-id) services-map)])]]))

