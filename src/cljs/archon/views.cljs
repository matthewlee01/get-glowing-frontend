(ns archon.views
  (:require
   [re-frame.core :as re-frame]
   [archon.subs :as subs]
   [archon.events :as events]
   [cljss.core :as css :refer-macros [defstyles defkeyframes]]
   [cljss.reagent :refer-macros [defstyled]])
  (:require-macros [cljss.core]))
     
(defstyles title [font-size]
  {:font-size (str font-size "px")
   :color "#FFB6C1"
   :margin "10px 10px"})

(defstyles main-nav []
  {:width "auto"})
   
(defstyles input-class []
  {:margin "50px"})

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
   :font-size "18px"})
  
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


(defn show-vendor-info
    [id]
    (do (re-frame/dispatch [::events/request-vendor-info id])
        (re-frame/dispatch [::events/set-active-panel :vendor-info-panel])))

(defn city-form []
  [:div {:class (input-class)}
     [:label {:class (field-label 15)} "What city are you interested in:"]
     (input-field {:type "text"
                   :auto-focus true
                   :value @(re-frame/subscribe [::subs/city-name])
                   :on-input #(re-frame/dispatch [::events/city-name-change (-> % .-target .-value)])})
     (sexy-button {:on-click #(re-frame/dispatch [::events/submit-city])} "Enter")])


(defn ven-reg-form []
  [:div {:class (input-class)}
   [:div
     [:label {:class (field-label 15)} "Enter your email address:"]
     (input-field{:type "text"
                  :auto-focus true
                  :on-blur #(re-frame/dispatch [::events/vr-email-change (-> % .-target .-value)])})]
   [:div
     [:label {:class (field-label 15)} "Enter your password:"]
     (input-field{:type "text"
                  :auto-focus true
                  :on-blur #(re-frame/dispatch [::events/vr-pwd-change (-> % .-target .-value)])})]
   [:div
     [:label {:class (field-label 15)} "Confirm your password:"]
     (input-field{:type "text"
                  :auto-focus true
                  :on-blur #(re-frame/dispatch [::events/vr-conf-pwd-change (-> % .-target .-value)])})]
   [:div
    [:label {:class (field-label 15)} "First Name:"]
    (input-field{:type "text"
                 :auto-focus true
                 :on-blur #(re-frame/dispatch [::events/vr-first-name-change (-> % .-target .-value)])})]
   [:div
    [:label {:class (field-label 15)} "Last Name:"]
    (input-field{:type "text"
                 :auto-focus true
                 :on-blur #(re-frame/dispatch [::events/vr-last-name-change (-> % .-target .-value)])})]
   [:div
    [:label {:class (field-label 15)} "Phone:"]
    (input-field{:type "text"
                 :auto-focus true
                 :on-blur #(re-frame/dispatch [::events/vr-phone-change (-> % .-target .-value)])})]

   (sexy-button {:on-click #(re-frame/dispatch [::events/submit-vendor-form])} "Enter")])

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
      (map vendor-card vendors))])

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
      (sexy-button "Login")
      (sexy-button "Sign Up")]
    (condp = @(re-frame/subscribe [::subs/active-panel])
      :vendors-panel [vendors-panel]
      :city-input-panel [city-form]
      :vendor-info-panel [vendor-info-panel]
      :vendor-signup-panel [ven-reg-form]
      :thanks-for-registering-panel [thanks-panel])]) 
