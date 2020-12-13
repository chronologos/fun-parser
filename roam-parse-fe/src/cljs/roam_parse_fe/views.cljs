(ns roam-parse-fe.views
  (:require
   [clojure.string]
   [cljs.pprint]
   [instaparse.core :as insta]
   [re-frame.core :as re-frame]
   [roam-parse-fe.subs :as subs]
   [roam-parse-fe.localstorage :as ls]
   [cljs.tools.reader :refer [read-string]]
   [cljs.js :refer [empty-state eval js-eval]]
   [reagent.core :as r]))

;; (defonce component-message (r/atom ""))
;; (defonce component-syntax (r/atom ""))

(defn main-form []
  (print "re-render main-form")
  (let [component-message (r/atom (ls/get-item "message"))
        component-syntax (r/atom (ls/get-item "syntax"))]
    (print "msg" @component-message)
    (fn []
      [:form
       [:div.form-group
        [:button.btn.btn-primary
         {:on-click (fn [e]
                      (.preventDefault e)
                      (re-frame/dispatch [:new-message @component-message])
                      (re-frame/dispatch [:new-syntax @component-syntax])
                      (ls/set-item! "message" @component-message)
                      (ls/set-item! "syntax" @component-syntax))}
         "Parse!"]]
       [:div.form-group.row
        [:label.col-form-label.col-sm-2 "message"]
        [:textarea.form-control.col-sm-10
         {:name :message
          :rows 5
          :on-change (fn [e]
                       (.preventDefault e)
                       (reset! component-message (-> e .-target .-value)))}
         @component-message]]
       [:div.form-group.row
        [:label.col-form-label.col-sm-2 "syntax"]
        [:textarea.form-control.col-sm-10
         {:name :syntax
          :rows 10
          :on-change (fn [e]
                       (.preventDefault e)
                       (reset! component-syntax (-> e .-target .-value)))}
         @component-syntax]]])))

(def as-and-bs
  (insta/parser
   "S = AB*
     AB = A B
     A = 'a'+
     B = 'b'+"))

(defn parse-panel []
  (let [message (re-frame/subscribe [::subs/message])
        syntax (re-frame/subscribe [::subs/syntax])
        s "[:h1 \"hello\"]"]
    (print "re-render parse-panel")
    (fn []
      (let  [parser (try (insta/parser @syntax) (catch :default e (print "uhoh: " e ", syntax = " @syntax) as-and-bs))
             parsed (try (parser @message) (catch :default e (str "invalid parse: " e ", parser = " parser)))
            ;;  parsed (as-and-bs @message)
             ]
        (print parsed)
        [:div
         [:p
          [:b "parse tree: "]
          [:code (with-out-str (cljs.pprint/pprint parsed))]]
         [:p "render\n-----"
          (eval (empty-state)
                (read-string s)
                {:eval js-eval}
                (fn [result] (:value result)))]
         [:p "end render -----"]]))))

(defn main-panel []
  (print "re-render main")
  [:div.container-fluid
   [parse-panel]
   [main-form]])

