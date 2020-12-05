(ns roam-parse-fe.views
  (:require
   [clojure.string]
   [instaparse.core :as insta]
   [re-frame.core :as re-frame]
   [roam-parse-fe.subs :as subs]
   [reagent.core :as r]))

;; (defonce component-message (r/atom ""))
;; (defonce component-syntax (r/atom ""))

(defn main-form []
  (print "re-render main-form")
  (let [component-message (r/atom "")
        component-syntax (r/atom "")]
    (fn []
      [:form
       [:p "syntax and msg: " @component-syntax " " @component-message]
       [:div.form-group.row
        [:label.col-form-label.col-sm-2 "message"]
        [:textarea.form-control.col-sm-10
         {:name :message
          :on-change (fn [e]
                       (.preventDefault e)
                       (reset! component-message (-> e .-target .-value)))}]]
       [:div.form-group.row
        [:label.col-form-label.col-sm-2 "syntax"]
        [:textarea.form-control.col-sm-10
         {:name :syntax
          :on-change (fn [e]
                       (.preventDefault e)
                       (reset! component-syntax (-> e .-target .-value)))}]]
       [:div.form-group
        [:button.btn.btn-primary
         {:on-click (fn [e]
                      (.preventDefault e)
                      (re-frame/dispatch [:new-message @component-message])
                      (re-frame/dispatch [:new-syntax @component-syntax]))}
         "Parse!"]]])))

(def as-and-bs
  (insta/parser
   "S = AB*
     AB = A B
     A = 'a'+
     B = 'b'+"))

(defn parse-panel []
  (let [name (re-frame/subscribe [::subs/name])
        message (re-frame/subscribe [::subs/message])
        syntax (re-frame/subscribe [::subs/syntax])]
    (print "re-render parse-panel")
    (fn []
      (let  [
             parser (try (insta/parser @syntax) (catch :default e (print "uhoh: " e ", syntax = " @syntax) as-and-bs))
             parsed (try (parser @message) (catch :default e (str "invalid parse: " e ", parser = " parser)))
            ;;  parsed (as-and-bs @message)
             ]
        (print parsed)
        [:h1 "Hello from " @name]
        [:p
         [:b "parse tree: "]
         [:code (str parsed) ]]))))

(defn main-panel []
  (print "re-render main")
  [:div.container-fluid
   [parse-panel]
   [main-form]])

