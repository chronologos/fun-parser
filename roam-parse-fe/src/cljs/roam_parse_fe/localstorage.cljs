(ns roam-parse-fe.localstorage)

(def message-key "message")
(def syntax-key "syntax")

(defn set-item!
  "Set `key' in browser's localStorage to `val`."
  [key val]
  (.setItem (.-localStorage js/window) key val))

(defn get-item
  "Returns value of `key' from browser's localStorage."
  ([key]
   (.getItem (.-localStorage js/window) key))
  ([key default]
   (let [item (.getItem (.-localStorage js/window) key)]
     (if (= item "") default item))))


(defn remove-item!
  "Remove the browser's localStorage value for the given `key`"
  [key]
  (.removeItem (.-localStorage js/window) key))