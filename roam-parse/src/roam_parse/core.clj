(ns roam-parse.core
  (:require [instaparse.core :as insta]))

(def as-and-bs
  (insta/parser
   "S = AB*
     AB = A B
     A = 'a'+
     B = 'b'+"))

(as-and-bs "aaaaabbbaaaabb")