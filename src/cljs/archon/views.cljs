(ns archon.views
  (:require
    [re-frame.core :as re-frame]
    [archon.subs :as subs]
    [archon.events :as events]
    [archon.auth0 :as auth0]
    [cljss.core :refer-macros [inject-global]]
    [archon.ven-reg.views :as ven-reg]
    [archon.ven-reg.events :as vre]
    [archon.ven-list.views :as ven-list]
    [archon.ven-reg.events :as ven-reg-events]
    [archon.city.views :as city]
    [archon.calendar.views :as calendar]
    [archon.ven-details.views :as ven-details]
    [archon.routes :as routes]
    [archon.common-css :as css])
  (:require-macros [cljss.core]))


(inject-global
  {
   :body {:font-family "Arial, Verdana, sans-serif"}})


(defn service-input []
  [:div
    [:label "Service"]
    [:input {:type "text"
             :auto-focus true}]])

(defn thanks-panel []
  [:div
    [:p "Thanks for registering."]
    [:button {:on-click #(re-frame/dispatch [::events/take-me-back])} "Return"]])

(defn nav-buttons []
  [:div {:class (css/main-nav)}
      (if (check-auth) 
        (css/NavBarElement {:on-click #(re-frame/dispatch [::ven-reg-events/show-vendor-email-form])}"Become a vendor"))
      (css/NavBarElement "Help")
      (if (check-auth)
        (css/NavBarElement {:on-click on-sign-out} "Sign out")
        (css/NavBarElement {:on-click #(.show auth0/lock)} "Login/Sign up"))])
  
(defn main-panel []
  [:div
    [:h1 {:class (css/huge-title) :on-click #(re-frame/dispatch [::events/set-active-panel ::routes/city-panel])}
         @(re-frame/subscribe [::subs/app-name])]
    [nav-buttons]
    (condp = @(re-frame/subscribe [::subs/active-panel])
      ::routes/vendor-list-panel [ven-list/panel]
      ::routes/city-panel [city/panel]
      ::routes/vendor-details-panel [ven-details/panel]
      ::routes/vendor-signup-panel [ven-reg/panel]
      :thanks-for-registering-panel [thanks-panel]
      ::routes/calendar-panel [calendar/panel])]) 


