(ns archon.ven-reg.events
  (:require
    [re-frame.core :as rf]
    [archon.db :as db]
    [archon.config :as config]
    [clojure.spec.alpha :as s]
    [clojure.string :as str]))

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

(rf/reg-event-db
  ::vr-email-change
  (fn [db [_ vendor-email]]
    (assoc-in db [:vendor-reg :vr-email] vendor-email)))

(rf/reg-event-db
  ::vr-pwd-change
  (fn [db [_ vendor-pwd]]
    (assoc-in db [:vendor-reg :vr-pwd] vendor-pwd)))

(rf/reg-event-db
  ::vr-conf-pwd-change
  (fn [db [_ vendor-conf-pwd]]
    (assoc-in db [:vendor-reg :vr-conf-pwd] vendor-conf-pwd)))

(rf/reg-event-db
  ::vr-first-name-change
  (fn [db [_ vendor-first-name]]
    (assoc-in db [:vendor-reg :vr-first-name] vendor-first-name)))

(rf/reg-event-db
  ::vr-last-name-change
  (fn [db [_ vendor-last-name]]
    (assoc-in db [:vendor-reg :vr-last-name] vendor-last-name)))

(rf/reg-event-db
  ::vr-phone-change
  (fn [db [_ vendor-phone]]
    (assoc-in db [:vendor-reg :vr-phone] vendor-phone)))

(rf/reg-event-db
  ::vr-address-change
  (fn [db [_ vendor-address]]
    (let [[num name] (str/split vendor-address #" " 2)]
    (-> db
        (assoc-in [:vendor-reg :vr-addr-num] num)
        (assoc-in [:vendor-reg :vr-addr-name] name))
    )))

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
    (assoc-in db [:vendor-reg :vr-addr-phone] vendor-postal)))

(rf/reg-event-db
  ::submit-vendor-form
  (fn [db _]
    (if (and (-> db :vendor-reg :vr-email (valid-email))
             (-> db :vendor-reg :vr-addr-num (valid-num)))
        (assoc db :active-panel :thanks-for-registering-panel))
      db))

