(ns archon.views
  (:require
   [re-frame.core :as re-frame]
   [archon.subs :as subs]
   ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "this is a re-frame project"]
     ]))
