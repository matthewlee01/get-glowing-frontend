(ns archon.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::app-name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
  ::active-panel
  (fn [db _]
    (:active-panel db)))

(re-frame/reg-sub
  ::vendor-list
  (fn [db _]
    (:vendor-list db)))

(re-frame/reg-sub
  ::city-name
  (fn [db _]
    (:city-name db)))
