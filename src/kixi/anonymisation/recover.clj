(ns kixi.anonymisation.recover
  (require [kixi.anonymisation.hide :as hide]))


(defn word->recovered-word [lookup word]
  (get word lookup word))

(defn line->recovered-line [lookup line]
  (->> line
      (hide/line->words)
      (map (partial word->recovered-word lookup))
      (hide/words->line)))

(defn from-chunk  [lookup txt]
  (->> txt
      hide/chunk->lines
      (map (partial line->recovered-line lookup))
      (hide/lines->chunk)))

(defn from-file   [lookup-file file])
