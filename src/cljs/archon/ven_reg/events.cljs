(ns archon.ven-reg.events
  (:require
    [re-frame.core :as rf]
    [archon.db :as db]
    [archon.config :as config]
    [archon.routes :as routes]
    [archon.events :as events]
    [clojure.spec.alpha :as s]
    [clojure.string :as str]
    [ajax.core :as ajax :refer [json-request-format
                                json-response-format]]))

(rf/reg-event-fx
  ::submit-vendor-registration
  (fn [world _]
    (let [db (:db world)
          vendor-info (:vendor-reg db)
          {:keys [vr-email
                  vr-first-name
                  vr-last-name
                  vr-addr-num
                  vr-addr-name
                  vr-addr-city
                  vr-addr-state
                  vr-phone]} vendor-info
          user-id (get-in db [:user-info :user-id])
          vendor {:user-id user-id
                  :name_first vr-first-name
                  :name_last vr-last-name
                  :email vr-email
                  :addr_str_num vr-addr-num
                  :addr_str_name vr-addr-name
                  :addr_city vr-addr-city
                  :addr_state vr-addr-state
                  :phone vr-phone}]
      {:http-xhrio {:method  :post
                    :uri    config/ven-reg-url
                    :params vendor
                    :timeout 3000
                    :format (json-request-format)
                    :response-format (json-response-format {:keywords? true})
                    :on-success [::good-result]
                    :on-failure [::bad-result]}})))

(rf/reg-event-fx
  ::good-result
  (fn [_  _]
    (let [url-string (routes/name-to-url ::routes/thanks-panel)]
      {:navigate url-string})))

(rf/reg-event-fx
  ::bad-result
  events/show-error)

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$") ; some characters + @ + some characters + 2-63 letters

(def num-regex #"^[0-9]+")

(s/def ::email-type (s/and string? #(re-matches email-regex %)))

(s/def ::num-type (s/and string? #(re-matches num-regex %)))

(defn valid-email
  [email]
  (let [checked-email (s/conform ::email-type email)]
    (if (= checked-email ::s/invalid)
      (do (js/alert "oops! bad email")
        false)
      (do (js/alert "good email!")
        checked-email))))

(defn valid-num
  [num]
  (let [checked-num (s/conform ::num-type num)]
    (if (= checked-num ::s/invalid)
      (do (js/alert "oops! bad address number")
        false)
      (do (js/alert "good address number!")
        checked-num))))

(rf/reg-event-fx
  ::show-vendor-signup-form
  (fn [world _]
    ;; this is where we push the new URL onto the browser history 
    ;; to start the navigation to the registration form
    (let [db (:db world)
          clean-prev-state (dissoc db :prev-state) ; don't want nested prev states
          url-string (routes/name-to-url ::routes/vendor-signup-panel)]

      {:db (assoc db :prev-state clean-prev-state)
       :navigate url-string})))

(rf/reg-event-db
  ::vr-email-change
  (fn [db [_ vendor-email]]
    (assoc-in db [:vendor-reg :vr-email] vendor-email)))

(rf/reg-event-db
  ::vr-first-name-change
  (fn [db [_ vendor-first-name]]
    (assoc-in db [:vendor-reg :vr-first-name] vendor-first-name)))

(rf/reg-event-db
  ::vr-last-name-change
  (fn [db [_ vendor-last-name]]
    (assoc-in db [:vendor-reg :vr-last-name] vendor-last-name)))

(rf/reg-event-db
  ::vr-address-change
  (fn [db [_ vendor-address]]
    (let [[num name] (str/split vendor-address #" " 2)]
      (-> db
        (assoc-in [:vendor-reg :vr-addr-num] num)
        (assoc-in [:vendor-reg :vr-addr-name] name)))))

(rf/reg-event-db
  ::vr-city-change
  (fn [db [_ vendor-city]]
    (assoc-in db [:vendor-reg :vr-addr-city] vendor-city)))

(rf/reg-event-db
  ::vr-state-change
  (fn [db [_ vendor-state]]
    (assoc-in db [:vendor-reg :vr-addr-state] vendor-state)))

(rf/reg-event-db
  ::vr-postal-change
  (fn [db [_ vendor-postal]]
    (assoc-in db [:vendor-reg :vr-addr-postal-code] vendor-postal)))

(rf/reg-event-db
  ::vr-phone-change
  (fn [db [_ vendor-phone]]
    (assoc-in db [:vendor-reg :vr-phone] vendor-phone)))

(rf/reg-event-db
  ::submit-vendor-form
  (fn [db _]
    (if (-> db :vendor-reg :vr-addr-num (valid-num))
      (assoc db :active-panel :thanks-for-registering-panel)
      db)))
