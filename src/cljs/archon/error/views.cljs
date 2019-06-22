(ns archon.error.views
  (:require [re-frame.core :as rf]
            [archon.subs :as subs]
            [archon.error.css :as css]))

(defn panel []
  (let [{:keys [status status-text]} @(rf/subscribe [::subs/last-error-payload])]
    [:div {:class (css/error-message)}
      [:p "Oh no there was an error! :("]
      [:div (str "Error status: " status)]
      [:div (str status-text)]
      [:div (cond
               (= status 0) "try again lol"
               (>= status 500) "something's wrong with the server"
               (>= status 400) "bad request!"
               :else "")]
    ]
    ))
