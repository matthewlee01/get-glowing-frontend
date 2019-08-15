(ns archon.v-services.views
  (:require [re-frame.core :as rf]
            [archon.common-css :as css]
            [archon.subs :as subs]
            [archon.v-services.events :as vse]
            [cljss.reagent :refer-macros [defstyled]]
            [cljss.core :as cljss :refer-macros [defstyles]]))

(defstyles flex-container []
           {:display "flex"
            :justify-content "space-between"
            :flex-wrap "wrap"
            ::&:after {:clear "both"}
            ::cljss/media {[:only :screen :and [:min-width "800px"]] {:width "700px"}
                           [:only :screen :and [:min-width "1000px"]] {:width "990px"}}})

(defstyled service-card-div :div
           ;;           {:background-color "#efa2bf"
           {:background-color css/color-card-background
            :display "inline-block"
            :width "600px"
            :height "400px"
            :margin "10px"
            :text-align "left"
            :box-shadow "4px 4px 8px 0 #686868"})

(defstyled CardButton :button
           {:background-color css/color-card-background
            :border "1px"
            :border-radius "10px"
            :border-color "#FFFFFF"
            :box-sizing "border-box"
            :margin "5px 0"
            :padding "4px 10px"
            :font-size "16px"
            :font-weight "bold"
            :cursor "pointer"
            :min-width "80px"
            :height "42px"
            :transition "0.3s"
            :&:hover {:opacity "0.5"}})

(defstyled BottomMenu :div
          {:background-color "#333300"
           :text-align "right"})

(defn input-field [label editable? default-value type edit-event service-id]
  (let [border (if editable? "1px solid red" "0px solid red")
        background (if editable? "#FFFFFF" css/color-glow-main)]
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
        editable? (= service-id currently-active-id)]
    ^{:key service-id}
    (service-card-div
      [input-field "Name:" editable? s-name "text" ::vse/name-change service-id]
      [input-field "Type:" editable? s-type "text" ::vse/type-change service-id]
      [input-field "Duration:" editable? s-duration "number" ::vse/duration-change service-id]
      [input-field "Price:" editable? (/ s-price 100) "number" ::vse/price-change service-id]
      [input-field "Description:" editable? s-description "text" ::vse/description-change service-id]
      (BottomMenu
        (if editable?
          [:div
            [css/SubmitButton {:on-click #(rf/dispatch [::vse/submit-selected])} "Submit"]
            [css/SubmitButton {:on-click #(rf/dispatch [::vse/cancel-selected])} "Cancel"]]
          [:div
            [CardButton {:on-click #(rf/dispatch [::vse/edit-selected service-id])}
             "edit"]
            [CardButton "activate"]
            [css/SubmitButton "delete"]])))))

(defn panel []
  (let [services-map @(rf/subscribe [::subs/services-map])
        active-service-id @(rf/subscribe [::subs/active-service-id])]
    [:div 
     [css/SubmitButton {:on-click #(rf/dispatch [::vse/add-new-selected])} "Add new service"]
     [:div [:h1 "Existing Services"]]
     [:div {:class (flex-container)}
      (if (= services-map {})
          [:div {:style {:background-color "#DFDFAA"
                         :display "block"}} 
             [:h2 "No Services Currently Defined"]]
          [:div
            (map #(service-card % active-service-id) services-map)])]]))

