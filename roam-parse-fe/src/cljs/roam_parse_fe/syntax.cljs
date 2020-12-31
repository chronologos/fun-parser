(ns roam-parse-fe.syntax
  (:require
   [clojure.string]))

(def default-syntax
  (clojure.string/join
   "\n" ["<t>   = (<'\\n'>? s)*"
         "<s>  = hl | bold | italic | ref | blockref | alias | render | latex | insect | text"
         "<textchars> = #'[a-zA-Z0-9+()=;\\., ]+'"
         "text = textchars | !markers '*' | !markers '^' (* exclude bold/highlight markers so that we can do greedy regex match (which is faster than using textchar+) *)"
         "markers = bold-mk | hl-mk"
         "<bold-mk> = <'**'>"
         "<hl-mk> = <'^^'>"
         "hl = hl-mk t-no-hl hl-mk"
         "bold = bold-mk t-no-bold bold-mk"
         "italic = '__' t '__'"
         "<refOrText> = refText | ref"
         "<refText> = #'[a-zA-Z0-9+()=*; ]+'"
         "ref = <'[['> refOrText+ <']]'>"
         "blockref = '((' text '))'"
         "alias = <'['> text <']'> <'('> (text | ref) <')'>"
         "render = '{{' text '}}'"
         "latex = '$$' #'.'* '$$'  (* this path is slower and needs separate handling *)"
         "insect = <'{{'> <'>insect '> insectExpr <'}}'> (* transform fn will handle calculation of text *)"
         "insectExpr = #'[a-zA-Z0-9+()=*; ]+'"
         "<t-no-hl>   = (<'\\n'>? s-no-hl)*"
         "<s-no-hl>  = bold | italic | ref | blockref | alias | render | latex | insect | text"
         "<t-no-bold>   = (<'\\n'>? s-no-bold)*"
         "<s-no-bold>  = hl | italic | ref | blockref | alias | render | latex | insect | text"]))