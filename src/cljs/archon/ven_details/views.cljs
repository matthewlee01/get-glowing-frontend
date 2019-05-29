(ns archon.ven-details.views
  (:require
    [re-frame.core :as rf]
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]
    [archon.events :as events]
    [archon.subs :as subs]
    [archon.ven-details.events :as vde]
    [archon.calendar.events :as cev]
    [archon.ven-details.css :as vd-css]
    [archon.common-css :as common-css]))


(defn make-service-card-div [service-info]
  "makes a vector with a index number and the div for the service card;
   used with map-indexed to group services into columns"
  (let [ {:keys [s_description s_duration s_name s_price s_type]} service-info]
     [:div [:div {:class (vd-css/service-card-title 16)} s_name]
           [:div {:class (vd-css/service-card-box)}
             [:div s_type]
             [:div s_duration]
             [:div s_price]
             [:div s_description]]]))
    

(defn panel[]
    (let [{:keys [vendor_id 
                  name_first 
                  services 
                  profile_pic]} 
          @(rf/subscribe [::subs/vendor-details])

          prof-pic (str "/" profile_pic)]    ;; TODO - this hack actually needs a fix 
                                             ;; on the server to send an absolute path
                                             ;; for now hack the path to be absolute
     [:div [:img {:src prof-pic
                  :alt prof-pic
                  :class (vd-css/list-pfp 200)}]
           [:p vendor_id]
           [:p name_first]
           [:br]
           [:div {:class (vd-css/service-card-array)} (map make-service-card-div services)]
           (common-css/nav-button {:on-click #(rf/dispatch [::cev/get-calendar vendor_id])} 
                        "View Calendar")]))
   
