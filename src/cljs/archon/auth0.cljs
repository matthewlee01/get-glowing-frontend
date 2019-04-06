(ns archon.auth0
  (:require [re-frame.core :as re-frame]
            [archon.config :as config]
            [archon.events :as events]
            [archon.subs :as subs]
            [cljsjs.auth0-lock]))

(def lock
  (let [client-id (:client-id config/auth0)
        domain (:domain config/auth0)
        options (clj->js {})]
    (js/Auth0Lock. client-id domain options)))

(defn handle-profile-response [error profile] 
  "handles auth0 profile request response"
  (config/debug-out (str "Auth0 user profile: "
                      (js->clj profile)))
  (let [profile-clj (js->clj profile :keywordize-keys true)]
    (re-frame/dispatch [::events/set-profile profile-clj])
    (println (str "name: " (profile-clj :name)))))

(defn on-authenticated
  "called by the auth0 lock upon authentication"
  [auth-result-js]
  (config/debug-out (str "Auth0 authentication result: "
                      (js->clj auth-result-js)))
  (let [auth-result-clj (js->clj auth-result-js :keywordize-keys true)
        access-token (:accessToken auth-result-clj)]
    (re-frame/dispatch [::events/set-auth-result auth-result-clj])
    (.getUserInfo lock access-token handle-profile-response)
    (println (str "access token: " access-token))))

(.on lock "authenticated" on-authenticated)



