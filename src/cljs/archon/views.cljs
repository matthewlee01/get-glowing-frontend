(ns archon.views
  (:require
    [re-frame.core :as re-frame]
    [archon.subs :as subs]
    [archon.events :as events]
    [archon.auth0 :as auth0]
    [cljss.core  :refer-macros [inject-global]]
    [archon.ven-reg.views :as ven-reg]
    [archon.ven-reg.events :as vre]
    [archon.ven-list.views :as ven-list]
    [archon.city.views :as city]
    [archon.calendar.views :as calendar]
    [archon.ven-details.views :as ven-details]
    [archon.routes :as routes]
    [archon.common-css :as css])
  (:require-macros [cljss.core]))
     
(inject-global {:body {:font-family "Arial, Verdana, sans-serif"}})

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
   ;[:div {:class (main-nav)}
     (css/NavBarElement "Help")
     (if @(re-frame/subscribe [::subs/user-info])
       [:span
         (css/NavBarElement {:id "two" :on-click #(re-frame/dispatch [::vre/show-vendor-signup-form])}"Become a vendor")
         (css/NavBarElement {:id "one" :on-click #(re-frame/dispatch [::events/sign-out])} "Sign out")]
       (css/NavBarElement {:on-click #(.show auth0/lock)} "Login/Sign up"))])

(defn logged-in-welcome [sz user]
  [:span {:style {:float "right"}}
    [css/label (str "Welcome back, " (:name-first user))]
    [:div {:style {:float "right"}}
      [:img {:src (:avatar user)
             :class (css/avatar sz)}]]])

(defn logged-out-welcome [sz]
  [:span {:style {:float "right"}}
    [css/label (str "")]
    [:div {:class (css/faux-avatar sz)
           :style {:float "right"}}]])

(defn welcome-message [sz]
  (let [user @(re-frame/subscribe [::subs/user-info])]
    [:div {:class (css/header sz)}
      (if user
        (logged-in-welcome sz user)
        (logged-out-welcome sz))]))

(defn main-panel []
  [:div
    [welcome-message 50]
    [:h1 {:class (css/huge-title) :on-click #(re-frame/dispatch [::events/set-active-panel ::routes/city-panel])}
         @(re-frame/subscribe [::subs/app-name])]
    [nav-buttons]
    (condp = @(re-frame/subscribe [::subs/active-panel])
      ::routes/vendor-list-panel [ven-list/panel]
      ::routes/city-panel [city/panel]
      ::routes/vendor-details-panel [ven-details/panel]
      ::routes/vendor-signup-panel [ven-reg/panel]
      ::routes/thanks-panel [thanks-panel]
      ::routes/calendar-panel [calendar/panel])]) 


