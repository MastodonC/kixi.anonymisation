(ns kixi.anonymisation
  (require
   [clojure-stemmer.porter.stemmer :as stemmer]
   [pandect.algo.sha3-256 :as sha3]))

(defn ts [] (quot (System/currentTimeMillis) 1000))

(defn chunk->lines [chunk]
  (clojure.string/split chunk #"\n+"))

(defn- words->line [words]
  (str (clojure.string/join " " words) "\n") )

(defn line->words [line]
  (let [words (clojure.string/split line #"\s+")]
    (remove clojure.string/blank? words)))

(defn- word->hash [word]
  (-> word
      (str ts)
      sha3/sha3-256))

(defn- anon-word [lookup word]
  (let [root-word  (stemmer/stemming word)
        hashed-word (get @lookup root-word (word->hash root-word))]
    (swap! lookup assoc root-word hashed-word)
    hashed-word))

(defn- line->anon-line [lookup line]
  (->> line
       line->words
       (map (partial anon-word lookup))
       words->line))

(defn anonymise-chunk [chunk]
  (let [lookup (atom {})
        lines (chunk->lines chunk)
        anon-chunk (map (partial line->anon-line lookup) lines)]
    (clojure.string/join "\n" anon-chunk)))

(defn anonymise-file [in-file out-file]
  (let [lookup (atom {})]
    (with-open [reader (clojure.java.io/reader in-file)]
      (with-open [writer (clojure.java.io/writer out-file)]
        (doseq [line (line-seq reader)]
          (.write writer (line->anon-line lookup line)))))))
