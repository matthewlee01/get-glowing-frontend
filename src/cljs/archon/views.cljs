(ns archon.views
  (:require
    [re-frame.core :as re-frame]
    [archon.subs :as subs]
    [archon.events :as events]
    [archon.auth0 :as auth0]
    [cljss.core :as css :refer-macros [defstyles defkeyframes]]
    [cljss.reagent :refer-macros [defstyled]]
    [archon.ven-reg.views :as ven-reg]
    [archon.ven-list.views :as ven-list]
    [archon.ven-list.events :as ven-list-events]
    [archon.city.views :as city])
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
  {:height "90px"
   :width "290px"
   :padding "10px"
   :margin "0px 10px"
   :border "5px solid #FFB6C1"})

(defstyles service-card-array []
  {:display "flex"
   :flex-wrap "wrap"
   :&:after {:clear "both"}
   ::css/media {[:only :screen :and [:min-width "700px"]] {:width "700px"}
   	            [:only :screen :and [:min-width "1050px"]] {:width "1050px"}}})

(defstyles calendar-day-box []
  {:display "flex"
   :padding "12px"
   :width "100%"})


(defstyles time-slot-class [color]
  {:background-color color
   :width "90%"
   :margin "1px"
   :padding "2px 12px"
   :text-align "center"
   :display "inline-block"
   :height "50px"
   :line-height "50px"
   :vertical-align "middle"
   :color "white"
   :box-sizing "border-box"
   :font-size "16px"
   :font-family "Arial"})

(defstyles calendar-column []
  {:width "375px"
   :height "100%"
   :vertical-align "middle"
   :margin "2px"})

(defstyles centre-day-box []
  {:display "flex"
   :height "100%"})

(defstyles outer-day-box []
  {:display "flex"
   :height "100%"
   ::css/media {[:only :screen :and [:max-width "1050px"]] {:display "none"}}})

(defstyles time-label-box []
  {:display "flex"
   :height "100%"
   :width "100px"})

(defstyles side-time-label []
  {:width "100%"
   :vertical-align "33px"
   :height "50px"
   :display "inline-block"})

  
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

(defn check-auth
  "probably change this later"
  []
  (and (not-empty @(re-frame/subscribe [::subs/auth-result]))
       (not-empty @(re-frame/subscribe [::subs/profile]))))


(defn service-input []
  [:div
    [:label "Service"]
    [:input {:type "text"
             :auto-focus true}]])


(defn make-service-card-div [service-info]
  "makes a vector with a index number and the div for the service card;
   used with map-indexed to group services into columns"
  (let [ {:keys [s_description s_duration s_name s_price s_type]} service-info]
     [:div [:div {:class (service-card-title 16)} s_name]
           [:div {:class (service-card-box)}
             [:div s_type]
             [:div s_duration]
             [:div s_price]
             [:div s_description]]
    ]))


(defn vendor-info-panel []
    (let [{:keys [vendor_id name_first services profile_pic]} @(re-frame/subscribe [::subs/current-vendor-info])]
     [:div [:img {:src profile_pic
                  :alt profile_pic
                  :class (list-pfp 200)}]
           [:p vendor_id]
           [:p name_first]
           [:br]
           [:div {:class (service-card-array)} (map make-service-card-div services)]
           (sexy-button {:on-click #(re-frame/dispatch [::events/set-active-panel :calendar-panel])} "View Calendar")]))
   
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

(defn generate-displayed-times
  "generates an array of time-slots given a start time, slot count, and increment,
  ex// [0, 2, 60] would return [[0 59] [60 119]]. used for generating the
  vector for the displayed hours on each calendar day"
  [start-time slot-count increment]
  (->> (range slot-count)
       (map (fn [slot]
              (+ (* slot increment) start-time)))
       (map (fn [start]
              [start (+ start (- increment 1))]))
       (vec)))
              
(defn minute-int-to-time-string
  "takes a minute count (i.e. 90) and converts it to a time string ('1:30 AM')"
  [minute-int]
  (let [hours (quot minute-int 60)
        mins (mod minute-int 60)]
    (str
      (if (> hours 12)
        (- hours 12)
        (if (= hours 0) ;; hour 0 is a special case between 24 and 12 hour time
          12
          hours))
      ":"
      (if (= 0 mins)
        "00"
        mins)
      (if (> hours 11)
        " PM"
        " AM"))))

(def sample-available [[0 599]])
(def sample-booked1 [[300 359] [420 479]])
(def sample-booked2 [])
(def sample-booked3 [[360 429] [720 779] [840 900]])
(def sample-date1 "May 1")
(def sample-date2 "May 2")
(def sample-date3 "May 3")
(def DISPLAYED_TIMES (generate-displayed-times 180 13 30)) 
  
(defn time-label
  [time-int]
  [:label {:class (side-time-label)} (minute-int-to-time-string time-int)])
  
(defn available-slot
  [[start-time _]]
  [:div {:class (time-slot-class "#FFB6C1")
         :on-click #(js/alert (str "this is time-slot " start-time))} (str (minute-int-to-time-string start-time) " - Available")])

(defn unavailable-slot
  [[start-time _]]
  [:div {:class (time-slot-class "#7a7978")
         :on-click #(js/alert (str "this is time-slot " start-time))} (str (minute-int-to-time-string start-time) " - Unavailable")])

(defn time-within-chunk?
  "checks whether a time-slot is contained within a time-chunk"
  [time-slot time-chunk]
  (and (>= (first time-slot) (first time-chunk))
       (<= (second time-slot) (second time-chunk))))
  
(defn time-within-coll?
  "checks to see if the time slot is contained anywhere within
  the collection of time-chunks"
  [time-slot time-coll]
  (->> time-coll
       (map (fn [time-chunk] (time-within-chunk? time-slot time-chunk)))
       (every? false?)        
       (not)))

(defn calendar-day-column
  "creates a column of time slots based on the available times"
  [date displayed-times available-time booked-time]
  (->> [:div {:class (calendar-column)}
        [date]
        (map (fn [time-slot]
               (if (and (time-within-coll? time-slot available-time)
                        (not (time-within-coll? time-slot booked-time)))
                 (available-slot time-slot)
                 (unavailable-slot time-slot))) displayed-times)]
       (mapcat #(if (sequential? %) % [%])) ;; flattens the elements created in the map into the parent div
       (vec)))

(defn time-label-column
  "creates a column of time labels based on the displayed times"
  [displayed-times]
  (->> [:div {:class (calendar-column)}
        (map (fn [[start-time _]] (time-label start-time)) displayed-times)]
       (mapcat #(if (sequential? %) % [%])) ;; flattens the elements created in the map into the parent div
       (vec)))

(defn calendar-panel
  []
  [:div {:class (calendar-day-box)}
   [:div {:class (time-label-box)} (time-label-column DISPLAYED_TIMES)]
   [:div {:class (outer-day-box)} (calendar-day-column sample-date1 DISPLAYED_TIMES sample-available sample-booked1)]
   [:div {:class (centre-day-box)} (calendar-day-column sample-date2 DISPLAYED_TIMES sample-available sample-booked2)]
   [:div {:class (outer-day-box)} (calendar-day-column sample-date3 DISPLAYED_TIMES sample-available sample-booked3)]])

(defn nav-buttons []
  [:div {:class (main-nav)}
      (if (check-auth) 
        (sexy-button {:on-click #(re-frame/dispatch [::events/show-vendor-email-form])}"Become a vendor"))
      (sexy-button "Help")
      (if (check-auth)
        (sexy-button {:on-click on-sign-out} "Sign out")
        (sexy-button {:on-click #(.show auth0/lock)} "Login/Sign up"))])
  
(defn main-panel []
  [:div
    [:h1 {:class (title 80) :on-click #(re-frame/dispatch [::events/set-active-panel :city-input-panel])}
         @(re-frame/subscribe [::subs/app-name])]
    (nav-buttons)
    (condp = @(re-frame/subscribe [::subs/active-panel])
      :vendor-list-panel [ven-list/panel]
      :city-input-panel [city/panel]
      :vendor-info-panel [vendor-info-panel]
      :vendor-signup-panel (ven-reg/panel)
      :thanks-for-registering-panel [thanks-panel]
      :calendar-panel [calendar-panel])]) 


