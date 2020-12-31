(ns roam-parse-fe.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::message
 (fn [db]
   (:message db)))

(re-frame/reg-sub
 ::syntax
 (fn [db]
   (:syntax db)))

(re-frame/reg-sub
 ::testcases
 (fn [db]
   (:testcases db)))