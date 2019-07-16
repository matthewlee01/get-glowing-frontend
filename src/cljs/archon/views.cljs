(ns archon.views
  (:require
    [re-frame.core :as re-frame]
    [archon.subs :as subs]
    [archon.events :as events]
    [cljss.core  :refer-macros [inject-global]]
    [archon.ven-reg.views :as ven-reg]
    [archon.ven-reg.events :as vre]
    [archon.ven-list.views :as ven-list]
    [archon.city.views :as city]
    [archon.calendar.views :as calendar]
    [archon.ven-details.views :as ven-details]
    [archon.error.views :as error]
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
    [:button {:on-click #(re-frame/dispatch [::events/vendor-logon])} "Return"]])

(defn nav-buttons []
  (let [user @(re-frame/subscribe [::subs/user-info])
        vendor? (:is-vendor user)]
    [:div {:class (css/main-nav)}
      (css/NavBarElement "Help")
      (if user 
        [:span
          (when-not vendor?  ;; only show option to become a vendor when not already a vendor
            (css/NavBarElement {:id "two" :on-click #(re-frame/dispatch [::vre/show-vendor-signup-form])}"Become a vendor"))
          (css/NavBarElement {:id "one" :on-click #(re-frame/dispatch [::events/sign-out])} "Sign out")]
        (css/NavBarElement {:on-click #(re-frame/dispatch [::events/login-initiated])} "Login/Sign up"))]))

(defn logged-in-welcome [sz user]
  [:span {:class (css/welcome-message)}
    [css/label (str "Welcome back, " (:name-first user))]
    [:div {:style {:float "right"}}
      [:img {:src (:avatar user)
             :class (css/avatar sz)}]]])

(defn logged-out-welcome [sz]
  [:span {:style {:float "right"}}
    [css/label (str "")]
    [:div {:class (css/faux-avatar sz)
           :style {:float "right"}}]])

(defn header [sz]
  (let [user @(re-frame/subscribe [::subs/user-info])
        vendor? (:is-vendor user)]                          
    [:div {:class (css/header sz vendor?)}
      [nav-buttons]
      (if user
        (logged-in-welcome sz user)
        (logged-out-welcome sz))]))

(defn error-modal []
  (if-let [error-msg @(re-frame/subscribe [::subs/error-msg])]
    [:div {:class (css/error-modal-bg)}
      [:div {:class (css/error-modal-box)}
        [:span {:class (css/error-modal-close)
                :on-click #(re-frame/dispatch [::events/set-error-msg nil])} 
         (goog.string/unescapeEntities "&times")] ;;this returns a unicode "x" symbol string     
        [:p {:class (css/error-msg)} error-msg]]]))
  
(defn main-panel []
  [:div
    (error-modal)
    [header 50]
    [:h1 {:class (css/huge-title) :on-click #(re-frame/dispatch [::events/navigate-to-url (routes/name-to-url ::routes/city-panel)])}
         @(re-frame/subscribe [::subs/app-name])]
    (condp = @(re-frame/subscribe [::subs/active-panel])
      ::routes/vendor-list-panel [ven-list/panel]
      ::routes/city-panel [city/panel]
      ::routes/vendor-details-panel [ven-details/panel]
      ::routes/vendor-signup-panel [ven-reg/panel]
      ::routes/thanks-panel [thanks-panel]
      ::routes/calendar-panel [calendar/panel]
      ::routes/error-panel [error/panel]
      nil)])  ;; normally the path should match one of the above, except at first startup.


