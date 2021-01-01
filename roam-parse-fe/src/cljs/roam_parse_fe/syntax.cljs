(ns roam-parse-fe.syntax
  (:require
   [clojure.string]))

(def default-syntax
  (clojure.string/join
   "\n" ["<t>   = (<'\\n'>? s)*"
         "<s>  = mark | b | i | ref | blockref | alias | render | latex | insect | text"
         "<textchars> = #'[a-zA-Z0-9+()=;\\., ]+'"
         "text = textchars | !markers '*' | !markers '^' | !markers '_' (* exclude bold/highlight/italic markers so that we can do greedy regex match (which is faster than using textchar+) *)"
         "markers = b-tok | hl-tok | i-tok"
         "<b-tok> = <'**'>"
         "<hl-tok> = <'^^'>"
         "<i-tok> = <'__'>"
         "mark = hl-tok t-no-mark hl-tok"
         "b = b-tok t-no-bold b-tok"
         "i = i-tok t-no-i i-tok"
         "<refOrText> = refText | ref"
         "<refText> = #'[a-zA-Z0-9+()=*; ]+'"
         "ref = <'[['> refOrText+ <']]'>"
         "blockref = '((' text '))'"
         "alias = <'['> text <']'> <'('> (text | ref) <')'>"
         "render = '{{' text '}}'"
         "latex = '$$' #'.'* '$$'  (* this path is slower and needs separate handling *)"
         "insect = <'{{'> <'>insect '> insectExpr <'}}'> (* transform fn will handle calculation of text *)"
         "insectExpr = #'[a-zA-Z0-9+()=*; ]+'"
         "<t-no-mark>   = (<'\\n'>? s-no-mark)*"
         "<s-no-mark>  = b | i | ref | blockref | alias | render | latex | insect | text"
         "<t-no-i>   = (<'\\n'>? s-no-i)*"
         "<s-no-i>  = mark | b | ref | blockref | alias | render | latex | insect | text"
         "<t-no-bold>   = (<'\\n'>? s-no-bold)*"
         "<s-no-bold>  = mark | i | ref | blockref | alias | render | latex | insect | text"]))