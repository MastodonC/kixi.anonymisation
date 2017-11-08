(ns kixi.anonymisation.parser)

(defn chunk->sentences [chunk]
  (clojure.string/split chunk #"\.(\s|\n|$)+"))

(defn sentences->chunk [lines]
  (->> lines
       (reduce (fn [chunk line] (concat chunk (str line ". "))) "" )
       clojure.string/join
       clojure.string/trim))

(defn words->sentence [words]
  (clojure.string/join " " words))

(defn sentence->words [line]
  (let [words (clojure.string/split line #"\s+")]
    (remove clojure.string/blank? words)))
