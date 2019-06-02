(ns archon.ven-details.views
  (:require
    [re-frame.core :as rf]
    [cljss.core :refer-macros [defstyles]]
    [cljss.reagent :refer-macros [defstyled]]
    [archon.subs :as subs]
    [archon.ven-details.events :as vde]
    [archon.ven-details.css :as vd-css]
    [archon.common-css :as css]))


(defn make-service-card-div [service-info]
  "makes a vector with a index number and the div for the service card;
   used with map-indexed to group services into columns"
  (let [ {:keys [s_description s_duration s_name s_price s_type]} service-info]
    ^{:key service-info}
     [:div [:div {:class (vd-css/service-card-title 16)} s_name]
           [:div {:class (vd-css/service-card-box)}
             [:div s_type]
             [:div s_duration]
             [:div s_price]
             [:div s_description]]]))
    

(defn panel[]
    (let [{:keys [vendor_id 
                  name
                  name_first
                  name_last
                  summary
                  services 
                  profile_pic]} 
          @(rf/subscribe [::subs/vendor-details])
          first-last-name (str name_first " " name_last)
          fullname (if name name first-last-name)
          prof-pic (str "/" profile_pic)]    ;; TODO - this hack actually needs a fix 
                                             ;; on the server to send an absolute path
                                             ;; for now hack the path to be absolute
     [:div [vd-css/profile-img {:src prof-pic
                                :alt prof-pic}]
           [vd-css/profile-title fullname]
           [vd-css/profile-summary summary]
           [:br]
           [:div {:class (vd-css/service-card-array)}
             (map make-service-card-div services)]

           (css/NavBarElement {:on-click #(rf/dispatch [::vde/get-calendar vendor_id "2019-07-18"])} ;;using a default date for now, should actually access db for date
                            "View Calendar")]))
  
