(ns archon.config)

(def debug?
  ^boolean goog.DEBUG)

(defn globar-root []
  (if debug?
    "http://localhost:8888/"
    "http://archon.j3mc.ca:8888/"))

(def root-url (globar-root))

(def graphql-url (str root-url "graphql"))
(def calendar-url (str root-url "calendar"))
(def ven-reg-url (str root-url "vendor"))
(def login-url (str root-url "login"))

(defn debug-out [params]
  (when debug?
    (js/alert params)))

;; stores the id and domain for the auth0 client
(def auth0 
  {:client-id  "N5TPQFZSKLbfYds40YYv4i31v5577szV"
   :domain  "n00b.auth0.com"
   :audience "api.getglowing.com"}) ;; audience specifies the auth0 api id that the access 
                                    ;; token will be used with
