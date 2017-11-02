(ns kixi.anonymisation
  (require
   [clojure-stemmer.porter.stemmer :as stemmer]
   [pandect.algo.sha3-256 :as sha3]))

(defn ts [] (quot (System/currentTimeMillis) 1000))

(defn words->line [words]
  (str (clojure.string/join " " words) "\n") )

(defn line->words [line]
  (let [words (clojure.string/split line #"\s+")]
    (remove clojure.string/blank? words)))

(defn hash [word]
  (-> word
      (str ts)
      sha3/sha3-256))

(defn anon-word [lookup word]
  (let [root-word (stemmer/stemming word)]
    (get lookup root-word (hash root-word))))

(defn line->anon-line [line lookup]
  (->> line
       line->words
       (map (partial anon-word lookup))
       words->line))

(defn anonymise [in-file out-file]
  (let [lookup {}]
    (with-open [reader (clojure.java.io/reader in-file)]
      (with-open [writer (clojure.java.io/writer out-file)]
        (doseq [line (line-seq reader)]
          (.write writer (line->anon-line line lookup)))))))
