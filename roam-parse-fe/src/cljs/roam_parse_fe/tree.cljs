(ns roam-parse-fe.tree
  (:require
   [instaparse.core :as insta]))

(def as-and-bs
  (insta/parser
   "S = AB*
     AB = A B
     A = 'a'+
     B = 'b'+"))

(defn spans
  "Generate spans for a given tree, useful for debugging."
  [t]
  (if (sequential? t)
    (cons {:node (first t) :span (insta/span t)} (map spans (next t)))
    t))

(defn regen-tree [t]
  (if (sequential? t)
    (cons (insta/span t) (map spans (next t)))
    t))

(defn trans-insect-expr
  "Transform for insect expressions. Faked using js/eval for now."
  ([expr]  [(js/eval expr)]))

(defn trans-ref
  "Transform for hyperlinks."
  [expr] [:a {:href expr} expr])

(defn augmented-parse 
  "Parse message `m` into an AST while performing transforms."
  [parser m]
  (let [tree (try (parser m) (catch :default e (str "invalid parse: " e ", parser = " parser)))
        augmented (insta/transform {:insectExpr trans-insect-expr
                                    :ref trans-ref
                                    :text str} tree)
        all-spans (spans augmented)]
    (print "unaugmented: " tree "\n\naugmented:" augmented "\n\nall-spans:" all-spans "\n\n")
    augmented))