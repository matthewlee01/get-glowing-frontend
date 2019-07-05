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
               (= status 0) "Connection error.  The server may be down or there may be a network problem.  If the problem persists please contact support."
               (>= status 500) "Server error. The request couldn't be processed or the server is down.  If the problem persists, please contact support."
               (>= status 400) "Permission denied.  Are you logged in?"
               :else "")]]))
    
    
