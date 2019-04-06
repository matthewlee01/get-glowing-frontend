(ns archon.auth0
  (:require [re-frame.core :as re-frame]
            [archon.config :as config]
            [cljsjs.auth0-lock]))

(def lock
  (let [client-id (:client-id config/auth0)
        domain (:domain config/auth0)
        options (clj->js {})]
    (js/Auth0Lock. client-id domain options)))

(defn handle-profile-response [error profile] 
  "handles auth0 profile request response"
  (config/debug-out (str "Auth0 user profile: "
                      (js->clj profile))))

(defn on-authenticated
  "called by the auth0 lock upon authentication"
  [auth-result-js]
  (config/debug-out (str "Auth0 authentication result: "
                      (js->clj auth-result-js)))
  (let [auth-result-clj (js->clj auth-result-js :keywordize-keys true)
        access-token (:accessToken auth-result-clj)]
    (.getUserInfo lock access-token handle-profile-response)))

(.on lock "authenticated" on-authenticated)



