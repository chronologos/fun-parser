(ns roam-parse-fe.views
  (:require
   [clojure.string]
   [cljs.pprint]
   [fipp.edn :refer [pprint] :rename {pprint fipp}]
   [instaparse.core :as insta]
   [re-frame.core :as re-frame]
   [roam-parse-fe.subs :as subs]
   [roam-parse-fe.syntax :as syntax]
   [roam-parse-fe.tree :as tree]
   [roam-parse-fe.localstorage :as ls]
   [reagent.core :as r]))

(defn main-form []
  (print "re-render main-form")
  (let [component-message (r/atom (ls/get-item ls/message-key))
        component-syntax (r/atom (ls/get-item ls/syntax-key syntax/default-syntax))]
    (fn []
      [:form
       [:div.form-group.row
        [:label.col-form-label.col-sm-1 "message"]
        [:textarea.form-control.col-sm-9
         {:name :message
          :rows 5
          :value @component-message
          :on-change (fn [e]
                       (.preventDefault e)
                       (reset! component-message (-> e .-target .-value)))}]]
       [:div.form-group.row
        [:label.col-form-label.col-sm-1 "syntax (EBNF)"]
        [:textarea.form-control.col-sm-9
         {:name :syntax
          :value @component-syntax
          :rows 10
          :on-change (fn [e]
                       (.preventDefault e)
                       (reset! component-syntax (-> e .-target .-value)))}]]
       [:div.form-group.row
       [:table
        [:tbody
         [:tr
          [:td.col-form-label.col-sm-1 "test messages"]
          [:td.col-sm-5
           [:select {:name (str "update_status_" 1)
                     :on-change (fn [e]
                                  (.preventDefault e)
                                  (reset! component-message (-> e .-target .-value)))}
            [:option {:value "[[yes [[hi]]]]"} "Nested Refs"]
            [:option {:value "{{>insect 1+1*20}}"} "Insect Block"]
            [:option {:value "^^**Hello**^^ **^^Seattle^^**"} "Bold Highlights Mixed"]
            [:option {:value "**[link]([[innerref]])**"} "Bolded alias"]]]]]]
          [:div.col-sm-4
          [:button.btn.btn-primary.mr-2
           {:on-click (fn [e]
                        (.preventDefault e)
                        (re-frame/dispatch [:new-message @component-message])
                        (re-frame/dispatch [:new-syntax @component-syntax])
                        (ls/set-item! "message" @component-message)
                        (ls/set-item! "syntax" @component-syntax))}
           "Parse"]
          [:button.btn.btn-primary
           {:on-click (fn [e]
                        (.preventDefault e)
                        (re-frame/dispatch [:new-message @component-message])
                        (re-frame/dispatch [:new-syntax syntax/default-syntax])
                        (reset! component-syntax syntax/default-syntax)
                        (ls/set-item! "message" @component-message)
                        (ls/set-item! "syntax" @component-syntax))}
           "Reset Syntax"]]]])))

(defn parse-panel []
  (let [message (re-frame/subscribe [::subs/message])
        syntax (re-frame/subscribe [::subs/syntax])]
    (fn []
      (let  [parser (try (insta/parser @syntax) (catch :default e (print "error: " e ", syntax = " @syntax " - defaulting to as and bs syntax") tree/as-and-bs))
             tree (tree/augmented-parse parser @message)
             timed (with-out-str (time (try (parser @message) (catch :default e (str "invalid parse: " e ", parser = " parser)))))]

        (print (with-out-str (fipp tree {:width 70})))
        [:div
         [:div.row
          [:div.col-sm-1 [:p "parse tree - " timed]]
          [:textarea.form-control.col-sm-9
           {:value (with-out-str (fipp tree {:width 70}))
            :rows 10
            :readOnly true}]]]))))

(defn main-panel []
  (print "re-rendering main")
  [:div.container-fluid
   [parse-panel]
   [main-form]])
