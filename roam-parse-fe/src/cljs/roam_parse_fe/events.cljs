(ns roam-parse-fe.events
  (:require
   [re-frame.core :as re-frame]
   [roam-parse-fe.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 :new-message
 (fn [db [_ new-message]]
   (assoc db :message new-message)
   ))

(re-frame/reg-event-db
 :new-syntax
 (fn [db [_ new-syntax]]
   (if (not= (db :syntax) new-syntax) (print "[db event] syntax-changed to: " new-syntax))
   (assoc db :syntax new-syntax)))
