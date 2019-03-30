(ns archon.views
  (:require
   [re-frame.core :as re-frame]
   [archon.subs :as subs]
   [archon.events :as events]))
   


(defn show-vendor-info
	[id]
	(do (re-frame/dispatch [::events/request-vendor-info id])
			(re-frame/dispatch [::events/set-active-panel :vendor-info-panel])))

(defn city-input []
  [:div
    [:label "City"]
    [:input {:type "text"
             :auto-focus true
             :on-blur #(re-frame/dispatch [::events/city-name-change (-> % .-target .-value)])}]
    [:button {:on-click #(re-frame/dispatch [::events/submit-city :vendors-panel])} "Enter"]])

(defn service-input []
  [:div
    [:label "Service"]
    [:input {:type "text"
             :auto-focus true}]])

(defn vendor-card [vendor]
  (let [ {:keys [vendor_id name_first name_last addr_city profile_pic]} vendor]
    [:div {:on-click #(show-vendor-info vendor_id)}
     [:img {:src profile_pic
            :alt profile_pic}]
     [:p (str vendor_id " " name_first " " name_last " " addr_city)]]))

(defn vendors-panel []
  [:div
    (let [vendors @(re-frame/subscribe [::subs/vendor-list])]
      (map vendor-card vendors))])

(defn service-card [service-info]
	(let [ {:keys [s_description s_duration s_name s_price s_type]} service-info]
			[:p (str s_type " " s_name " " s_duration " " s_price " " s_description)]))



(defn vendor-info-panel []
	(let [{:keys [vendor_id name_first services]} @(re-frame/subscribe [::subs/current-vendor-info])]
	[:div 
		[:p vendor_id]
		[:p name_first]
		(map service-card services)
		]))

(defn main-panel []
  [:div
    [:h1 @(re-frame/subscribe [::subs/app-name])]
    [:div
      [:button "Become a vendor"]
      [:button "Help"]
      [:button "Login"]
      [:button "Sign Up"]]
    (condp = @(re-frame/subscribe [::subs/active-panel])
      :vendors-panel [vendors-panel]
      :city-input [city-input]
      :vendor-info-panel [vendor-info-panel])])
      