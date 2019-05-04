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

(defstyles service-card-title [top]
  {:position "relative"
   :top (str top "px")
   :padding "10px"
   :font-size "28px"
   })

(defstyles service-card-box []
  {;:height "80px"
   :width "290px"
   :padding "5px"
   :border "5px solid pink"})

(defstyles service-card-column []
  {:float "left"
   :width "100%"
   ::css/media {[:only :screen :and [:min-width "665px"]]
                {:width "330px"}}})

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

(defn check-auth
  "probably change this later"
  []
  (and (not-empty @(re-frame/subscribe [::subs/auth-result]))
       (not-empty @(re-frame/subscribe [::subs/profile]))))

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

(defn make-service-card-div [index service-info]
  "makes a vector with a index number and the div for the service card;
   used with map-indexed to group services into columns"
  (let [ {:keys [s_description s_duration s_name s_price s_type]} service-info]
    [index
     [:div [:div {:class (service-card-title 16)} s_name]
           [:div {:class (service-card-box)}
             [:div s_type]
             [:div s_duration]
             [:div s_price]
             [:div s_description]]
    ]]))

(defn group-service-cards [[even odd] [parity service-card-div]]
  "takes numbered service cards and distributes them into vectors for the two columns based on the number"
  (if (odd? parity)
    [even (conj odd service-card-div)]
    [(conj even service-card-div) odd])
  )

(defn service-array [service-list]
  "takes a list of service information, creates service cards and distributes them into the two columns"
  (let [service-cards (map-indexed make-service-card-div service-list)
       [odd even] (reduce group-service-cards [[:div {:class (service-card-column)}] [:div {:class (service-card-column)}]] service-cards)]
    [:div odd even]
  ))

(defn vendor-info-panel []
    (let [{:keys [vendor_id name_first services profile_pic]} @(re-frame/subscribe [::subs/current-vendor-info])]
     [:div [:img {:src profile_pic
            :alt profile_pic
            :class (list-pfp 200)}]
           [:p vendor_id]
           [:p name_first]
           [:br]
           (service-array services)]))
    
(defn thanks-panel []
  [:div
    [:p "Thanks for registering.  Check your inbox for an email we've sent to verify your email address.
    This email will contain a link to confirm and activate your account.  If you don't get this email
    in a few minutes, check your spam folder in case it's been mis-directed there."]
    [:button {:on-click #(re-frame/dispatch [::events/take-me-back])} "Return"]])

(defn on-sign-out []
  (do (re-frame/dispatch [::events/set-auth-result {}])
      (re-frame/dispatch [::events/set-profile {}])
      (if (= @(re-frame/subscribe [::subs/active-panel]) :vendor-signup-panel)
        (re-frame/dispatch [::events/set-active-panel :city-input-panel]))))

(defn nav-buttons []
  [:div {:class (main-nav)}
      (if (check-auth) 
        (sexy-button {:on-click #(re-frame/dispatch [::events/show-vendor-email-form])}"Become a vendor"))
      (sexy-button "Help")
      (if (check-auth)
        (sexy-button {:on-click on-sign-out} "Sign out" )
        (sexy-button {:on-click #(.show auth0/lock)} "Login/Sign up"))]
  )

(defn main-panel []
  [:div
    [:h1 {:class (title 80) :on-click #(re-frame/dispatch [::events/set-active-panel :city-input-panel])}
         @(re-frame/subscribe [::subs/app-name])]
    (nav-buttons)
    (condp = @(re-frame/subscribe [::subs/active-panel])
      :vendors-panel [vendors-panel]
      :city-input-panel [city-form]
      :vendor-info-panel [vendor-info-panel]
      :vendor-signup-panel (ven-reg/panel)
      :thanks-for-registering-panel [thanks-panel])]) 
