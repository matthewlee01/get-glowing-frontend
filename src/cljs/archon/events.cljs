(ns archon.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [archon.auth0 :as auth0]
   [archon.db :as db]
   [archon.routes :as routes]
   [archon.config :refer [debug-out login-url]]
   [ajax.core :as ajax :refer [json-request-format 
                               json-response-format
                               raw-response-format]]))
(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

;; want to get rid of this, or have it only available in debug mode?
(re-frame/reg-event-db
  ::set-active-panel
  (fn [db [_ value]]
    (assoc db :active-panel value)))

(re-frame/reg-event-fx
  ::navigate-to-url
  (fn [_ [_ url]]
    {:navigate url}))

;; this is our custom effect handler to handle the :navigate effect
;; passed from -fx event handlers
(re-frame/reg-fx
  :navigate     ; make this a global keyword as are the other plugins
  (fn [url]
    (routes/navigate-to! url)))

(re-frame/reg-event-db
  ::take-me-back
  (fn [db _]
    (let [prev-state (:prev-state db)
          prev-active-panel (:active-panel prev-state)
          prev-url (routes/match-to-url prev-active-panel)]
      (routes/navigate-to! prev-url)
      prev-state)))

(re-frame/reg-event-db 
  ::set-auth-result
  (fn [db [_ auth-result]]
    (assoc-in db [:user :auth-result] auth-result)))

(re-frame/reg-event-fx
  ::access-token-received
  (fn [_ [_ access-token]]
    {:http-xhrio {:method  :post
                  :uri    login-url
                  :params {:token access-token}
                  :timeout 3000
                  :format (json-request-format)
                  :response-format (json-response-format)
                  :on-success [::backend-login-successful access-token]
                  :on-failure [::bad-http-result]}}))


(re-frame/reg-event-db
  ::bad-http-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (debug-out (str "ERROR sending access token to backend for validation: " payload))
    (dissoc db :access-token)))

(re-frame/reg-event-db
  ::backend-login-successful
  (fn [db [_ access-token payload]]
    (let [user-info (clojure.walk/keywordize-keys payload)]
      (assoc db :user-info user-info :access-token access-token))))

(re-frame/reg-fx
  :login
  (fn [_]
    (debug-out "reg-fx :login")
    (auth0/login)))

(re-frame/reg-fx
  :silent-login
  (fn [_]
    (debug-out "reg-fx :silent-login")
    (auth0/silent-login)))

(re-frame/reg-event-fx
  ::login-initiated
  (fn [_ _]
    {:login nil}))

(re-frame/reg-event-fx
  ::login-silent-initiated
  (fn [_ _]
    {:silent-login nil}))

(re-frame/reg-fx
  :logout
  (fn []
    (auth0/logout)))

(re-frame/reg-event-fx
  ::sign-out
  (fn [world _]
    (let [new-db (dissoc (:db world) :access-token :user-info)]
      {:logout nil
       :db new-db})))


