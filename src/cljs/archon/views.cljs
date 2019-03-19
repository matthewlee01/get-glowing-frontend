(ns archon.views
  (:require
   [re-frame.core :as re-frame]
   [archon.subs :as subs]
   ))

(defn city-input []
  [:div
    [:label "City"]
    [:input {:type "text"
             :auto-focus true}]])

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
      [:h1 "glow"]
      [:div
        [:button "Become a vendor"]
        [:button "Help"]
        [:button "Login"]
        [:button "Sign Up"]]
      (city-input)]))
      

(defn service-input []
  [:div
    [:label "Service"]
    [:input {:type "text"
             :auto-focus true}]])

(defn vendors-panel []
  [:div
    [:h1 "glow"]
    [service-input]
    [:div
      [:ul
         [:li "Vendor1"]
         [:li "Vendor2"]
         [:li "Vendor3"]]]])
  

