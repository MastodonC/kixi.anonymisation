(ns kixi.anonymisation.hide
  (require
   [kixi.anonymisation.stemmer   :as stemmer]
   [kixi.anonymisation.tokeniser :as tokeniser]
   [kixi.anonymisation.parser    :as parser]
   [kixi.anonymisation.whitelist :as whitelist]
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

(defn- sentence->anon-sentence [lookup line]
  (->> line
       tokeniser/line->tokens
       (map (partial anon-word lookup))
       parser/words->sentence))

(defn from-chunk [chunk]
  (let [lookup (atom {})
        sentences (parser/chunk->sentences chunk)
        anon-sentences (map (partial sentence->anon-sentence lookup) sentences)
        anon-chunk (parser/sentences->chunk anon-sentences)]
    {:lookup @lookup
     :content anon-chunk}))

(defn- anonomise-file [in-file out-file]
  (let [lookup (atom {})]
     (with-open [reader (clojure.java.io/reader in-file)]
       (with-open [writer (clojure.java.io/writer out-file)]
         (doseq [line (line-seq reader)]
           (.write writer (sentence->anon-sentence lookup line)))))
     @lookup))

(defn from-file
  ([in-file out-file]
   (let [lookup (anonomise-file in-file out-file)]
     (spit "lookup.edn" (prn-str lookup))))
  ([in-file out-file whitelist-file]
   (let [lookup             (anonomise-file in-file out-file)
         lookup-whitelisted (whitelist/from-file lookup whitelist-file)]
     (spit "lookup.edn"              (prn-str lookup))
     (spit "lookup.edn.whitelisted") (prn-str lookup-whitelisted))))
