(ns kixi.anonymisation.recover
  (require [kixi.anonymisation.parser :as parser]))

(defn word->recovered-word [lookup word]
  (get lookup word word))

(defn line->recovered-line [lookup line]
  (->> line
      (parser/sentence->words)
      (map (partial word->recovered-word lookup))
      (parser/words->sentence)))

(defn from-chunk  [lookup txt]
  (let [reverse-lookup (clojure.set/map-invert lookup)]
    (->> txt
         parser/chunk->sentences
         (map (partial line->recovered-line reverse-lookup))
         (parser/sentences->chunk))))

(defn from-file [lookup-file file]
  (let [lookup (-> lookup-file
                   slurp
                   clojure.edn/read-string)
        chunk (slurp file)
        recovered-chunk (from-chunk lookup chunk)]
    (spit (str file ".recovered") recovered-chunk)))
