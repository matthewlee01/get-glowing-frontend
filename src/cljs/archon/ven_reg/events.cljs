(ns archon.ven-reg.events
  (:require
    [re-frame.core :as rf]
    [archon.db :as db]
    [archon.config :as config]))

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

