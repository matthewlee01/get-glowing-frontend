(ns archon.v-services.views
  (:require [re-frame.core :as rf]
            [archon.common-css :as css]
            [archon.subs :as subs]
            [archon.v-services.events :as vse]
            [cljss.reagent :refer-macros [defstyled]]
            [cljss.core :as cljss :refer-macros [defstyles]]))

(defstyles flex-container []
           {:display "flex"
            :flex-wrap "wrap"
            ::&:after {:clear "both"}
            ::cljss/media {[:only :screen :and [:min-width "800px"]] {:width "700px"}
                           [:only :screen :and [:min-width "1000px"]] {:width "990px"}}})

(defstyled service-card-div :div
           ;;           {:background-color "#efa2bf"
           {:background-color "#e2c7d1"
            :display "inline-block"
            :width "600px"
            :height "680px"
            :margin "10px"
            :text-align "left"
            :box-shadow "4px 4px 8px 0 #686868"})

(defstyled CardButton :button
           {:background-color "#00FF77"
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

(defn service-card [service]
  (let [{:keys [s-description s-duration s-name s-price s-type]} service]
    ^{:key service}
    (service-card-div
      [:div
       [:p "Name: " s-name]
       [:p "Type:  " s-type]
       [:p "Duration: " s-duration]
       [:p "Cost:  $" (/ s-price 100)]
       [:p "Description:  "s-description]
       (BottomMenu
        [CardButton {:on-click #(rf/dispatch [::vse/edit-service-selected service])}
         "edit"]
        [CardButton "activate"]
        [css/SubmitButton "delete"])])))

(defn edit-service [active-service]
  [:div
   [:h1 "Post a new service"]
   [:div
    (css/TextInputField{:type "text"
                        :default-value (:s-name active-service)
                        :placeholder "Service Name"
                        :auto-focus true
                        :on-change #(rf/dispatch [::vse/name-change (-> % .-target .-value)])})]
   [:div
    (css/TextInputField{:type "text"
                        :default-value (:s-description active-service)
                        :placeholder "Description"
                        :auto-focus true
                        :on-blur #(rf/dispatch [::vse/description-change (-> % .-target .-value)])})]
   [:div
    (css/TextInputField{:type "text"
                        :default-value (:s-price active-service)
                        :placeholder "Price"
                        :auto-focus true
                        :on-blur #(rf/dispatch [::vse/price-change (-> % .-target .-value)])})]
   [:div
    (css/TextInputField{:type "text"
                        :default-value (:s-duration active-service)
                        :placeholder "Time for Service in Minutes"
                        :auto-focus true
                        :on-blur #(rf/dispatch [::vse/duration-change (-> % .-target .-value)])})]
   [:div
    (css/TextInputField{:type "text"
                        :default-value (:s-type active-service)
                        :placeholder "Type"
                        :auto-focus true
                        :on-blur #(rf/dispatch [::vse/type-change (-> % .-target .-value)])})]

   [css/SubmitButton {:on-click #(rf/dispatch [::vse/submit-service])} "Submit"]])

(defn panel []
  (let [services-list @(rf/subscribe [::subs/services-list])
        active-service @(rf/subscribe [::subs/active-service])]
    [:div {:class (flex-container)}
     [:div {:style {:justify-content "space-between"
                    :display "flex"}}
      [:h1
       "Existing Services"]]

     [:div
       (map service-card services-list)

       [edit-service active-service]]]))

