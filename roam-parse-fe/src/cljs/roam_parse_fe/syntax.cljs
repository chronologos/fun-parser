(ns roam-parse-fe.syntax
  (:require
   [clojure.string]))

(def default-syntax
  (clojure.string/join
   "\n" ["<t>   = s \"\\n\"? t | s | epsilon"
         "<s>  = highlight | bold | italic | ref | blockref | alias | render | latex | insect | text"
         "<textchars> = !\"**\" #\"[a-zA-Z0-9+()=*; ]\""
         "text = textchars+"
         "highlight = <\"^^\"> tnh <\"^^\">"
         "bold = <\"**\"> tnb <\"**\">"
         "italic = \"__\" t \"__\""
         "<refOrText> = refText | ref"
         "<refText> = #\"[a-zA-Z0-9+()=*; ]+\""
         "ref = <\"[[\"> refOrText+ <\"]]\">"
         "blockref = \"((\" text \"))\""
         "alias = <\"[\"> text <\"]\"> <\"(\"> (text | ref) <\")\">"
         "render = \"{{\" text \"}}\""
         "latex = \"$$\" #\".\"* \"$$\"  (* not implemented fully *)"
         "insect = <\"{{\"> <\">insect \"> insectExpr <\"}}\"> (* transform fn will handle calculation of text *)"
         "insectExpr = #\"[a-zA-Z0-9+()=*; ]+\""
         "<tnh>   = snh \"\\n\"? tnh | snh | epsilon"
         "<snh>  = bold | italic | ref | blockref | alias | render | latex | insect | text"
         "<tnb>   = snb \"\\n\"? tnb | snb | epsilon"
         "<snb>  = highlight | italic | ref | blockref | alias | render | latex | insect | text"
         ]))