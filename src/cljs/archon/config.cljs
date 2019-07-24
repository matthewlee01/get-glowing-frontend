(ns archon.config)

(def debug?
  ^boolean goog.DEBUG)

(def archon-root js/window.location.origin)

(def root-url (str "http://" js/window.location.hostname ":8888/"))

;; a list of backend API endpoints this application uses
(def graphql-url (str root-url "graphql"))
(def calendar-url (str root-url "calendar"))
(def booking-url (str root-url "booking"))
(def ven-reg-url (str root-url "vendor"))
(def login-url (str root-url "login"))
(def v-calendar-url (str root-url "v_calendar"))
(def v-bookings-url (str root-url "v_bookings"))
(def v-services-url (str root-url "services"))
(def edit-service-url (str root-url "service"))

(defn debug-out [params]
  (when debug?
    (console.log params)))

;; stores the id and domain for the auth0 client
(def auth0 
  {:client-id  "N5TPQFZSKLbfYds40YYv4i31v5577szV"
   :domain  "n00b.auth0.com"
   :audience "api.getglowing.com"}) ;; audience specifies the auth0 api id that the access 
                                    ;; token will be used with
