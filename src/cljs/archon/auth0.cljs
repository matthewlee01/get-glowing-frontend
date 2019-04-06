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
        options (clj->js {})]
    (js/Auth0Lock. client-id domain options)))

(defn handle-profile-response [error profile] 
  "handles auth0 profile request response"
  (config/debug-out (str "Auth0 user profile: "
                      (js->clj profile)))
  (let [profile-clj (js->clj profile :keywordize-keys true)] ;; turns the profile data into a clojure map and sends it into the db
    (re-frame/dispatch [::events/set-profile profile-clj]))) 

(defn on-authenticated
  "handles auth0 authentication request response"
  [auth-result-js]
  (config/debug-out (str "Auth0 authentication result: "
                      (js->clj auth-result-js)))
  (let [auth-result-clj (js->clj auth-result-js :keywordize-keys true)
        access-token (:accessToken auth-result-clj)]
    (re-frame/dispatch [::events/set-auth-result auth-result-clj]) ;; turns auth data into clojure map and sends it into db
    (.getUserInfo lock access-token handle-profile-response)))     ;; uses the access token to request the profile data

(.on lock "authenticated" on-authenticated)  ;; calls on-authenticated when the lock hears back from its request



