(ns kixi.recover
  (require [kixi.anonymisation :as anon]))


(defn word->recovered-word [lookup word]
  (get word lookup word))

(defn line->recovered-line [lookup line]
  (->> line
      (anon/line->words)
      (map (partial word->recovered-word lookup))
      (anon/words->line)))

(defn from-chunk  [lookup txt]
  (->> txt
      anon/chunk->lines
      (map (partial line->recovered-line lookup))
      (anon/lines->chunk)))

(defn from-file   [lookup-file file])
