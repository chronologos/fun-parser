(ns roam-parse-fe.views
  (:require
   [reagent.dom :as rdom]  
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
        component-syntax (r/atom (ls/get-item ls/syntax-key syntax/default-syntax))
        test-result (r/atom "not run")
        tcs @(re-frame/subscribe [::subs/testcases])]
    (re-frame/dispatch [:new-message @component-message])
    (re-frame/dispatch [:new-syntax syntax/default-syntax])
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
        [:label.col-form-label.col-sm-1 "test messages"]
        [:div.col-sm-3
         [:table
          [:tbody
           [:tr
            [:select {:name (str "update_status_" 1)
                      :on-blur (fn [e]
                                   (reset! component-message (-> e .-target .-value)))
                      :on-change (fn [e]
                                   (.preventDefault e)
                                   (reset! component-message (-> e .-target .-value)))}
             (for [{testname :name
                    testmsg :msg
                    _ :expect} tcs]
               ^{:key testname} [:option {:value testmsg} testname])]]]]]
        [:div.col-sm-6
         [:button.btn.btn-primary.mr-2
          {:on-click (fn [e]
                       (.preventDefault e)
                       (re-frame/dispatch [:new-message @component-message])
                       (re-frame/dispatch [:new-syntax @component-syntax])
                       (ls/set-item! ls/message-key @component-message)
                       (ls/set-item! ls/syntax-key @component-syntax))}
          "Parse"]
         [:button.btn.btn-primary.mr-2
          {:on-click (fn [e]
                       (.preventDefault e)
                       (re-frame/dispatch [:new-message @component-message])
                       (re-frame/dispatch [:new-syntax syntax/default-syntax])
                       (reset! component-syntax syntax/default-syntax))}
          "Load V1 Syntax"]
         [:button.btn.btn-primary.mr-2
          {:on-click (fn [e]
                       (.preventDefault e)
                       (let  [parser (try (insta/parser @component-syntax) (catch :default e (print "error: " e)))
                              results (time (doall (for [{testname :name
                                                          msg :msg
                                                          expect :expect} tcs]
                                                     (let [res  (tree/augmented-parse parser msg)]
                                                       (if (= (str res) expect)
                                                         (print "PASSED: " testname)
                                                         (print "FAILED TEST: " testname " expected: " expect " got: " (str res)))
                                                       (= (str res) expect)))))]
                         (reset! test-result
                                 (str (count (filter true? results))
                                      "/" (count tcs)))))}
          "Test All Cases"]
         [:div [:p "test success?: " @test-result]]]]])))

(defn parse-panel []
  (let [message (re-frame/subscribe [::subs/message])
        syntax (re-frame/subscribe [::subs/syntax])]
    (fn []
      (let  [parser (try (insta/parser @syntax) (catch :default e (print "error: " e ", syntax = " @syntax " - defaulting to as and bs syntax") tree/as-and-bs))
             tree-debug (parser @message :unhide :all)
             tree-augmented (tree/augmented-parse parser @message)
             timed (with-out-str (time (try (parser @message) (catch :default e (str "invalid parse: " e ", parser = " parser)))))]

        (print (with-out-str (fipp tree-debug {:width 70})))
        (try (rdom/render [:div tree-augmented] (.getElementById js/document "renderarea")) (catch :default e (print "error rendering (but that's fine)")))
        [:div
         [:div.row
          [:div.col-sm-1 [:p "parse tree (debug) - " timed]]
          [:textarea.form-control.col-sm-9
           {:value (with-out-str (fipp tree-debug {:width 70}))
            :rows 10
            :readOnly true}]]
         [:div.row
          [:div.col-sm-1 [:p "parse tree"]]
          [:textarea.form-control.col-sm-9
           {:value (with-out-str (fipp tree-augmented {:width 70}))
            :rows 10
            :readOnly true}]]
         ]))))

(defn main-panel []
  (print "re-rendering main")
  [:div.container-fluid
   [parse-panel]
   [main-form]])
