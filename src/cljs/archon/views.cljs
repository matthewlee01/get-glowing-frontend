(ns archon.views
  (:require
    [re-frame.core :as re-frame]
    [archon.subs :as subs]
    [archon.events :as events]
    [archon.auth0 :as auth0]
    [cljss.core :as css :refer-macros [defstyles defkeyframes]]
    [cljss.reagent :refer-macros [defstyled]]
    [archon.ven-reg.views :as ven-reg])
  (:require-macros [cljss.core]))
     
(defstyles title [font-size]
  {:font-size (str font-size "px")
   :color "#FFB6C1"
   :margin "10px 10px"})

(defstyles main-nav []
  {:width "auto"})
   
(defstyles input-class []
  {:margin "10px"})

(defstyles list-pfp [size]
  {:height (str size "px")
   :width (str size "px")})

(defstyles field-label [font-size]
  {:font-family "Arial"
   :padding "15px 15px"
   :color "#7a7978"
   :font-size (str font-size "px")})

(defstyled input-field :input
  {:padding "14px 14px"
   :font-size "18px"
   :vertical-align "center"})
  
(defstyled sexy-button :button
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

(defstyled submit-button :button
  {:background-color "#FFB6C1"
   :border "1px"
   :border-radius "50px"
   :box-sizing "border-box"
   :color "#7a7978"
   :margin "5px"
   :padding "4px 4px"
   :text-align "center"
   :text-decoration "none"
   :display "inline-block"
   :font-size "16px"
   :cursor "pointer"
   :font-family "Arial"
   :transition "0.3s"
   :width "22%"
   :max-width "120px"
   :height "58px"
   :vertical-align "middle"
   :&:hover {:opacity "0.9"}})
(defn show-vendor-info
    [id]
    (do (re-frame/dispatch [::events/request-vendor-info id])
        (re-frame/dispatch [::events/set-active-panel :vendor-info-panel])))

(defn city-form []
  [:div {:class (input-class)}
     [:label {:class (field-label 15)} "What city are you interested in:"]
     (input-field {:type "text"
                   :auto-focus true
                   :on-key-press #(if (= 13 (.-charCode %)) ;; 13 is code for enter key
                                    (re-frame/dispatch [::events/submit-city]))
                   :default-value @(re-frame/subscribe [::subs/city-name])
                   :on-input #(re-frame/dispatch [::events/city-name-change (-> % .-target .-value)])})
     (submit-button {:on-click #(re-frame/dispatch [::events/submit-city])}
                  "Enter")])

(defn service-input []
  [:div
    [:label "Service"]
    [:input {:type "text"
             :auto-focus true}]])

(defn vendor-card [vendor]
  (let [ {:keys [vendor_id name_first name_last addr_city profile_pic]} vendor]
    [:div {:on-click #(show-vendor-info vendor_id)}
     [:img {:src profile_pic
            :alt profile_pic
            :class (list-pfp 200)}]
     [:p (str vendor_id " " name_first " " name_last " " addr_city)]]))

(defn vendors-panel []
  [:div
    (let [vendors @(re-frame/subscribe [::subs/vendor-list])]
      (if (empty? vendors)
        [:p "Sorry, your search yielded no results in our database. Please try again!"]
        (map vendor-card vendors)))
    (sexy-button {:on-click #(re-frame/dispatch [::events/take-me-back])} "Return")])

(defn service-card [service-info]
    (let [ {:keys [s_description s_duration s_name s_price s_type]} service-info]
      [:p (str s_type " " s_name " " s_duration " " s_price " " s_description)]))

(defn vendor-info-panel []
    (let [{:keys [vendor_id name_first services]} @(re-frame/subscribe [::subs/current-vendor-info])]
     [:div 
           [:p vendor_id]
           [:p name_first]
           (map service-card services)]))
    
(defn thanks-panel []
  [:div
    [:p "Thanks for registering.  Check your inbox for an email we've sent to verify your email address.
    This email will contain a link to confirm and activate your account.  If you don't get this email
    in a few minutes, check your spam folder in case it's been mis-directed there."]
    [:button {:on-click #(re-frame/dispatch [::events/take-me-back])} "Return"]])

(defn main-panel []
  [:div
    [:h1 {:class (title 80)}
         @(re-frame/subscribe [::subs/app-name])]
    [:div {:class (main-nav)}
      (sexy-button {:on-click #(re-frame/dispatch [::events/show-vendor-email-form])}"Become a vendor")
      (sexy-button "Help")
      (sexy-button {:on-click #(.show auth0/lock)} "Login")
      (sexy-button "Sign Up")]
    (condp = @(re-frame/subscribe [::subs/active-panel])
      :vendors-panel [vendors-panel]
      :city-input-panel [city-form]
      :vendor-info-panel [vendor-info-panel]
      :vendor-signup-panel (ven-reg/panel)
      :thanks-for-registering-panel [thanks-panel])]) 
