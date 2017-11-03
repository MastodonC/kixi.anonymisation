(ns kixi.anonymisation.recover
  (require [kixi.anonymisation.hide :as anon]))

(defn word->recovered-word [lookup word]
  (get lookup word word))

(defn line->recovered-line [lookup line]
  (->> line
      (anon/line->words)
      (map (partial word->recovered-word lookup))
      (anon/words->line)))

(defn from-chunk  [lookup txt]
  (let [reverse-lookup (clojure.set/map-invert lookup)]
    (->> txt
         anon/chunk->lines
         (map (partial line->recovered-line reverse-lookup))
         (anon/lines->chunk))))

(defn from-file   [lookup-file file])