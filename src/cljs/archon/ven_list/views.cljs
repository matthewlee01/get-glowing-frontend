(ns archon.ven-list.views
  (:require
    [archon.ven-list.events :as vle]  ;; vendor list events
    [archon.events :as events]
    [archon.subs :as subs]
    [archon.common-css :as css]
    [re-frame.core :as rf]
    [cljss.core :as cljss :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]))


(defstyled vendor-card-div :div
;;           {:background-color "#efa2bf"
  {:background-color "#e2c7d1"
   :display "inline-block"
   :width "300px"
   :height "340px"
   :margin "10px"
   :text-align "center"
   :box-shadow "4px 4px 8px 0 #686868"})

(defstyled profile-img :img
  {:display "block"
   :margin-left "auto"
   :margin-right "auto"
   :margin-top "10px"
   :margin-bottom "20px"
   :object-fit "cover"
   :width "260px"
   :height "200px"})

(defstyled title :p
  {:font-size "16pt"
   :font-family "Arial"
   :margin "5px"})

(defstyled price-stats :p
  {:font-size "12pt"
   :margin "0px"})

(defstyled empty-stars :div
  {:display "inline-block" :position "relative"
   :font-family "FontAwesome"
   :font-size "18px"
   :&::before {:content "\f31b \f31b \f31b \f31b \f31b"}})

(defstyles filled-stars [percent-filled]
  {:position "absolute"
   :top "0"
   :left "0"
   :white-space "nowrap"
   :overflow "hidden"
   :width (str percent-filled "%")
   :&::before {:content "\f318 \f318 \f318 \f318 \f318"
               :color "#e2246a"}})

(defstyles vendor-card-flex []
  {:display "flex"
   :flex-wrap "wrap"
   ::&:after {:clear "both"}
   ::cljss/media {[:only :screen :and [:min-width "800px"]] {:width "700px"}
                  [:only :screen :and [:min-width "1000px"]] {:width "990px"}}})
     

(defn percentage [rating]
  (->> (/ rating 5.0)
       (* 100)
       Math/floor
       int))

(defn vendor-card [vendor]
  (let [{:keys [vendor_id name_first name_last addr_city profile_pic
                services_summary rating_summary]} vendor
        {:keys [min max]} services_summary
        {:keys [count average]} rating_summary
        rating% (percentage average)
        min$ (/ min 100)
        max$ (/ max 100)
        prof_pic (str "/" profile_pic)]  ;; TODO - this hack actually needs a fix
                                         ;; on the server to send an absolute path
                                         ;; for now hack the path to be absolute
    ^{:key vendor_id}
    [:div
      (vendor-card-div {:on-click #(rf/dispatch [::vle/request-vendor-details vendor_id])}
        (profile-img {:src prof_pic
                      :alt prof_pic})
        [:div
          (title (str  name_first " " name_last))
          (price-stats (str  "$" min$ " - $" max$))
          (empty-stars
            [:div {:class (filled-stars rating%)}])
          [:div [:div addr_city]]])]))

(defn panel []
  [:div
   (let [vendors @(rf/subscribe [::subs/vendor-list])]
    (if (empty? vendors)
       [:p "Sorry, your search yielded no results in our database. Please try again!"]
       [:div {:class (vendor-card-flex)} (map vendor-card vendors)]))
   (css/BackButton {:on-click #(rf/dispatch [::events/take-me-back])} "Return")])
