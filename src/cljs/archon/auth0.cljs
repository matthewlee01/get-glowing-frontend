(ns archon.auth0
  (:require [re-frame.core :as re-frame]
            [archon.config :as config]
            [archon.events :as events]
            [archon.subs :as subs]
            [cljsjs.auth0-lock]))

;; the lock object that links to auth0 and shows when you push the login button 
(def lock
  (let [client-id (:client-id config/auth0)
        domain (:domain config/auth0)
        options (clj->js {:auth { :audience (:audience config/auth0)}})]
    (js/Auth0Lock. client-id domain options)))

(defn on-authenticated
  "handles auth0 authentication request response"
  [auth-result-js]
  (config/debug-out (str "Auth0 authentication result: "
                      (js->clj auth-result-js)))
  (let [auth-result-clj (js->clj auth-result-js :keywordize-keys true)
        access-token (:accessToken auth-result-clj)]
    (re-frame/dispatch [::events/access-token-received access-token])))

(.on lock "authenticated" on-authenticated)  ;; calls on-authenticated when the lock hears back from its request



