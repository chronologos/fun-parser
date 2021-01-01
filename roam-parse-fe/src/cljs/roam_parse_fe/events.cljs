(ns roam-parse-fe.events
  (:require
   [re-frame.core :as re-frame]
   [roam-parse-fe.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   (assoc db/default-db
          :testcases
          [{:name "Nested Refs" :msg "[[Civis [[Romanus]] Sum]]" :expect "([:ref \"Civis \" [:ref \"Romanus\"] \" Sum\"])"}
           {:name "Insect Block" :msg "{{>insect 1+5*20}}" :expect "([:insect [101]])"}
           {:name "Bold Highlights Mixed" :msg "^^**Hello**^^ **^^Seattle^^**" :expect "([:mark [:b \"Hello\"]] \" \" [:b [:mark \"Seattle\"]])"}
           {:name "Bold Highlights Italics Mixed" :msg "^^**Hello**^^ **__**^^Seattle^^**__**" :expect "([:mark [:b \"Hello\"]] \" \" [:b [:i [:b [:mark \"Seattle\"]]]])
"}
           {:name "Bolded alias" :msg "**[link]([[innerref]])**" :expect "([:b [:alias \"link\" [:ref \"innerref\"]]])"}
           {:name "Generic 1" :msg "You have [[power over your mind]] not **outside events**. Realize this, and you will find strength." :expect "(\"You have \" [:ref \"power over your mind\"] \" not \" [:b \"outside events\"] \". Realize this, and you will find strength.\")"}
           ])))

(re-frame/reg-event-db
 :new-message
 (fn [db [_ new-message]]
   (assoc db :message new-message)))

(re-frame/reg-event-db
 :new-syntax
 (fn [db [_ new-syntax]]
   (if (not= (db :syntax) new-syntax) (print "[db event] syntax-changed to: " new-syntax))
   (assoc db :syntax new-syntax)))
