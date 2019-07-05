(ns archon.auth0
  (:require [re-frame.core :as re-frame]
            [archon.config :as config]
            [archon.subs :as subs]
            [cljsjs.auth0-lock]))

;; the authentication options are described at https://github.com/auth0/lock
;; and in better detail at https://auth0.com/docs/protocols/oauth2
(def jwt-options (clj->js {:auth {:audience (:audience config/auth0)}
                           :responseMode "web_message"}))

;; the lock object that links to auth0 and shows when you push the login button
(def lock
  (let [client-id (:client-id config/auth0)
        domain (:domain config/auth0)]

    (js/Auth0Lock. client-id domain jwt-options)))

(defn on-authenticated
  "handles auth0 authentication request response"
  [auth-result-js]
  (config/debug-out (str "Auth0 authentication result: "
                      (js->clj auth-result-js)))
  (let [auth-result-clj (js->clj auth-result-js :keywordize-keys true)
        access-token (:accessToken auth-result-clj)]
    (re-frame/dispatch [:archon.events/access-token-received access-token])))

(.on lock "authenticated" on-authenticated)  ;; calls on-authenticated when the lock hears back from its request

(defn login []
  (config/debug-out (str "regular login"))
  (.show lock))

(defn logout []
  (.logout lock (clj->js {:returnTo config/archon-root})))

(defn callback [error auth-result-js]
  (when error
    (config/debug-out (str "Error found in authentication callback: " (js->clj error))))

  ;; only send the token to the backend when the auth token is not nil, which will
  ;; be the case when silent login fails because the auth0 server doesn't have a cookie
  (when auth-result-js
    (on-authenticated auth-result-js)))

(defn silent-login []
  (config/debug-out (str "login-silent"))
  (.checkSession lock (clj->js {}) callback))



