(ns roam-parse-fe.views
  (:require
   [clojure.string]
   [cljs.pprint]
   [fipp.edn :refer [pprint] :rename {pprint fipp}]
   [instaparse.core :as insta]
   [re-frame.core :as re-frame]
   [roam-parse-fe.subs :as subs]
   [roam-parse-fe.localstorage :as ls]
   [cljs.tools.reader :refer [read-string]]
   [cljs.js :refer [empty-state eval js-eval]]
   [reagent.core :as r]))


(defn main-form []
  (print "re-render main-form")
  (let [component-message (r/atom (ls/get-item "message"))
        component-syntax (r/atom (ls/get-item "syntax"))]
    (print "msg" @component-message)
    (fn []
      [:form
       [:div.form-group.row
        [:div.col-sm-1
         [:button.btn.btn-primary
          {:on-click (fn [e]
                       (.preventDefault e)
                       (re-frame/dispatch [:new-message @component-message])
                       (re-frame/dispatch [:new-syntax @component-syntax])
                       (ls/set-item! "message" @component-message)
                       (ls/set-item! "syntax" @component-syntax))}
          "Parse!"]]
        [:label.col-form-label.col-sm-1 "message"]
        [:textarea.form-control.col-sm-10
         {:name :message
          :rows 5
          :value @component-message
          :on-change (fn [e]
                       (.preventDefault e)
                       (reset! component-message (-> e .-target .-value)))}]]
       [:div.form-group.row
        [:div.col-sm-1]
        [:label.col-form-label.col-sm-1 "syntax"]
        [:textarea.form-control.col-sm-10
         {:name :syntax
          :value @component-syntax
          :rows 10
          :on-change (fn [e]
                       (.preventDefault e)
                       (reset! component-syntax (-> e .-target .-value)))}]]])))

(def as-and-bs
  (insta/parser
   "S = AB*
     AB = A B
     A = 'a'+
     B = 'b'+"))


(defn text->hiccup
  "Convert newlines to [:br]'s."
  [text]
  (->> (clojure.string/split text "\n")
       (interpose [:br])
       (map #(if (string? %)
               %
               (with-meta % {:key (gensym "br-")})))))

(defn parse-panel []
  (let [message (re-frame/subscribe [::subs/message])
        syntax (re-frame/subscribe [::subs/syntax])
        s "[:p \"--placeholder for js eval--\"]"]
    (print "re-render parse-panel")
    (fn []
      (let  [parser (try (insta/parser @syntax) (catch :default e (print "uhoh: " e ", syntax = " @syntax) as-and-bs))
             parsed (try (parser @message) (catch :default e (str "invalid parse: " e ", parser = " parser)))
             timed (with-out-str (time (try (parser @message) (catch :default e (str "invalid parse: " e ", parser = " parser)))))]

        (print (with-out-str (fipp parsed {:width 70})))
        [:div
         [:div.row] [:p [:b "  parsed tree - " timed]]
         [:div.row
          [:div.col-sm-2]
          [:textarea.form-control.col-sm-10
           {:value (with-out-str (fipp parsed {:width 70}))
            :rows 10}]]
         [:div.row
          [:p (eval (empty-state)
                    (read-string s)
                    {:eval js-eval}
                    (fn [result] (:value result)))]]]))))

(defn main-panel []
  (print "re-render main")
  [:div.container-fluid
   [parse-panel]
   [main-form]])

