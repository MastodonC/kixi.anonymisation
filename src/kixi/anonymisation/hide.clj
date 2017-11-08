(ns kixi.anonymisation.hide
  (require
   [kixi.anonymisation.stemmer :as stemmer]
   [kixi.anonymisation.tokeniser :as tokeniser]
   [kixi.anonymisation.parser :as parser]
   [pandect.algo.sha3-256 :as sha3]))

(defn ts [] (quot (System/currentTimeMillis) 1000))

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
       tokeniser/line->tokens
       (map (partial anon-word lookup))
       parser/words->sentence))

(defn anonymise-chunk [chunk]
  (let [lookup (atom {})
        lines (parser/chunk->sentences chunk)
        anon-lines (map (partial line->anon-line lookup) lines)
        anon-chunk (parser/sentences->chunk anon-lines)]
    {:lookup @lookup
     :content anon-chunk}))

(defn anonymise-file [in-file out-file]
  (let [lookup (atom {})]
    (with-open [reader (clojure.java.io/reader in-file)]
      (with-open [writer (clojure.java.io/writer out-file)]
        (doseq [line (line-seq reader)]
          (.write writer (line->anon-line lookup line)))))))
