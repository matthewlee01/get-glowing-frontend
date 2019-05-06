(ns archon.ven-list.views
  (:require
    [archon.ven-list.events :as vle]  ;; vendor list events
    [archon.events :as events]
    [archon.subs :as subs]
    [re-frame.core :as rf]
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]))


(defstyled big-button :button
           {:background-color "#7a7978"
            :border "1px"
            :border-radius "4px"
            :box-sizing "border-box"
            :color "white"
            :margin "5px"
            :padding "2px 12px"
            :text-align "center"
            :text-decoration "none"
            :display "inline-block"
            :font-size "16px"
            :cursor "pointer"
            :font-family "Arial"
            :transition "0.3s"
            :width "22%"
            :max-width "120px"
            :height "62px"
            :vertical-align "top"
            :&:hover {:opacity "0.8"}})

(defstyled vendor-card-div :div
;;           {:background-color "#efa2bf"
           {:background-color "#e2c7d1"
            :display "inline-block"
            :width "300px"
            :height "300px"
            :margin "10px"
            :box-shadow "4px 4px 8px 0 #686868"})

(defstyled profile-pic :img
           {:display "block"
            :margin-left "auto"
            :margin-right "auto"
            :object-fit "cover"
            :width "200px"
            :height "200px"})

(defstyled title :p
           {:font-size "16pt"
            :text-align "center"
            :font-family "Arial"
            :margin "5px"})

(defstyled stats :p
           {:font-size "12pt"
            :text-align "center"
            :margin "0px"})

(defn show-vendor-info
  [id]
  (do (rf/dispatch [::vle/request-vendor-info id])
      (rf/dispatch [::events/set-active-panel :vendor-info-panel])))


(defn vendor-card [vendor]
  (let [{:keys [vendor_id name_first name_last addr_city profile_pic services_summary]} vendor
        {:keys [count min max]} services_summary
        min$ (/ min 100)
        max$ (/ max 100)]
    (vendor-card-div {:on-click #(show-vendor-info vendor_id)}
      (profile-pic {:src profile_pic
                    :alt profile_pic})
      [:div
        (title (str  name_first " " name_last))
        (stats (str  "$" min$ " - $" max$))
        [:p addr_city]])))

(defn panel []
  [:div
   (let [vendors @(rf/subscribe [::subs/vendor-list])]
     (if (empty? vendors)
       [:p "Sorry, your search yielded no results in our database. Please try again!"]
       (map vendor-card vendors)))
   (big-button {:on-click #(rf/dispatch [::events/take-me-back])} "Return")])
