# Evolution of the EBNF syntax

https://roamresearch.com/#/app/help/page/NYgRwJaQM

---
text-run    = span text-run | span
span        = strong | normal-text | link | page-ref
strong      = "**" text-run "**"
link      = "[" normal-texts "]" "(" normal-texts ")"
page-ref = "[[" normal-texts "]]"
normal-text = #"[a-zA-Z0-9 ]"
normal-texts = #"[a-zA-Z0-9 ]+"

[hello](world) [[this is a roam link]]

---

Sunday, December 13, 2020 3:35 PM

t   = s t | s
s  = text | highlight | strong | link | page-ref
strong = "**" t "**"
highlight = "^^" t "^^"
link = "[" text+ "]" "(" text+ ")"
page-ref = "[[" text+ "]]"
text = #"[a-zA-Z0-9 ]+" | conflict
conflict = "[" | "]"

---
2020-12-25

t   = s "\n"? t | s | epsilon
s  = text | highlight | bold | italic | ref | blockref | alias | render | latex | insect
text = #"[a-zA-Z0-9+()=*; ]+"
highlight = "^^" t "^^"
bold = "**" t "**"
italic = "__" t "__"
ref = "[[" (text | ref) "]]"
blockref = "((" text "))"
alias = "[" text "]" "(" (text | ref) ")"
render = "{{" text "}}"
latex = "$$" #"."* "$$"  (* this path is slower and needs separate handling *)

insect = "{{" ">insect" insectExpr "}}" (* transform fn will handle calculation of text *)
insectExpr = #"[a-zA-Z0-9+()=*; ]+"

---
[[hello]] this is an {{>insect 1+1*20}} block and ^^**nested stuff**^^ and [[nested [[blocks]]]]

<t>   = s "\n"? t | s | epsilon
<s>  = text | highlight | bold | italic | ref | blockref | alias | render | latex | insect
text = #"[a-zA-Z0-9+()=*; ]+"
highlight = "^^" t "^^"
bold = "**" t "**"
italic = "__" t "__"
ref = "[[" (text | ref) "]]"
blockref = "((" text "))"
alias = "[" text "]" "(" (text | ref) ")"
render = "{{" text "}}"
latex = "$$" #"."* "$$"  (* this path is slower and needs separate handling *)

insect = <"{{"> <">insect "> insectExpr <"}}"> (* transform fn will handle calculation of text *)
insectExpr = #"[a-zA-Z0-9+()=*; ]+"

---
<t>   = s "\n"? t | s | epsilon
<s>  = highlight | bold | italic | ref | blockref | alias | render | latex | insect | text
<textchars> = !"**" #"[a-zA-Z0-9+()=*; ]"
text = textchars+
highlight = <"^^"> tnh <"^^">
bold = <"**"> tnb <"**">
italic = "__" t "__"
<refOrText> = refText | ref
<refText> = #"[a-zA-Z0-9+()=*; ]+"
ref = <"[["> refOrText+ <"]]">
blockref = "((" text "))"
alias = "[" text "]" "(" (text | ref) ")"
render = "{{" text "}}"
latex = "$$" #"."* "$$"  (* this path is slower and needs separate handling *)
insect = <"{{"> <">insect "> insectExpr <"}}"> (* transform fn will handle calculation of text *)
insectExpr = #"[a-zA-Z0-9+()=*; ]+"
<tnh>   = snh "\n"? tnh | snh | epsilon
<snh>  = bold | italic | ref | blockref | alias | render | latex | insect | text
<tnb>   = snb "\n"? tnb | snb | epsilon
<snb>  = highlight | italic | ref | blockref | alias | render | latex | insect | text

---
2020-12-30
<t>   = s "\n"? t | s | epsilon
<s>  = highlight | bold | italic | ref | blockref | alias | render | latex | insect | text
<textchars> = !"**" #"[a-zA-Z0-9+()=*; ]"
text = textchars+
highlight = <"^^"> tnh <"^^">
bold = <"**"> tnb <"**">
italic = "__" t "__"
<refOrText> = refText | ref
<refText> = #"[a-zA-Z0-9+()=*; ]+"
ref = <"[["> refOrText+ <"]]">
blockref = "((" text "))"
alias = <"["> text <"]"> <"("> (text | ref) <")">
render = "{{" text "}}"
latex = "$$" #"."* "$$"  (* this path is slower and needs separate handling *)
insect = <"{{"> <">insect "> insectExpr <"}}"> (* transform fn will handle calculation of text *)
insectExpr = #"[a-zA-Z0-9+()=*; ]+"
<tnh>   = snh "\n"? tnh | snh | epsilon
<snh>  = bold | italic | ref | blockref | alias | render | latex | insect | text
<tnb>   = snb "\n"? tnb | snb | epsilon
<snb>  = highlight | italic | ref | blockref | alias | render | latex | insect | text

---
<t>   = s '\n'? t | s | epsilon
<s>  = highlight | bold | italic | ref | blockref | alias | render | latex | insect | text
<textchars> = #'[a-zA-Z0-9+()=;\., ]+'
text = textchars | !markers '*' | !markers '^'
markers = boldmarkers | highlightmarkers
<boldmarkers> = <'**'>
<highlightmarkers> = <'^^'>
highlight = highlightmarkers tnh highlightmarkers
bold = boldmarkers tnb boldmarkers
italic = '__' t '__'
<refOrText> = refText | ref
<refText> = #'[a-zA-Z0-9+()=*; ]+'
ref = <'[['> refOrText+ <']]'>
blockref = '((' text '))'
alias = <'['> text <']'> <'('> (text | ref) <')'>
render = '{{' text '}}'
latex = '$$' #'.'* '$$'  (* this path is slower and needs separate handling *)
insect = <'{{'> <'>insect '> insectExpr <'}}'> (* transform fn will handle calculation of text *)
insectExpr = #'[a-zA-Z0-9+()=*; ]+'
<tnh>   = snh '\n'? tnh | snh | epsilon
<snh>  = bold | italic | ref | blockref | alias | render | latex | insect | text
<tnb>   = snb '\n'? tnb | snb | epsilon
<snb>  = highlight | italic | ref | blockref | alias | render | latex | insect | text

---
<t>   = (<'\n'>? s)*
<s>  = highlight | bold | italic | ref | blockref | alias | render | latex | insect | text
<textchars> = #'[a-zA-Z0-9+()=;\., ]+'
text = textchars | !markers '*' | !markers '^'
markers = boldmarkers | highlightmarkers
<boldmarkers> = <'**'>
<highlightmarkers> = <'^^'>
highlight = highlightmarkers tnh highlightmarkers
bold = boldmarkers tnb boldmarkers
italic = '__' t '__'
<refOrText> = refText | ref
<refText> = #'[a-zA-Z0-9+()=*; ]+'
ref = <'[['> refOrText+ <']]'>
blockref = '((' text '))'
alias = <'['> text <']'> <'('> (text | ref) <')'>
render = '{{' text '}}'
latex = '$$' #'.'* '$$'  (* this path is slower and needs separate handling *)
insect = <'{{'> <'>insect '> insectExpr <'}}'> (* transform fn will handle calculation of text *)
insectExpr = #'[a-zA-Z0-9+()=*; ]+'
<tnh>   = snh '\n'? tnh | snh | epsilon
<snh>  = bold | italic | ref | blockref | alias | render | latex | insect | text
<tnb>   = snb '\n'? tnb | snb | epsilon
<snb>  = highlight | italic | ref | blockref | alias | render | latex | insect | text

---

```
"<t>   = (<'\\n'>? s)*"
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
"<s-no-bold>  = hl | italic | ref | blockref | alias | render | latex | insect | text"
```