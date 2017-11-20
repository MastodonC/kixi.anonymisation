(ns kixi.anonymisation.hide
  (require
   [kixi.anonymisation.stemmer   :as stemmer]
   [kixi.anonymisation.tokeniser :as tokeniser]
   [kixi.anonymisation.parser    :as parser]
   [kixi.anonymisation.whitelist :as whitelist]
   [pandect.algo.sha3-256 :as sha3]))

(defn ts [] (quot (System/currentTimeMillis) 1000))

(defn empty-lookup-resource [] (clojure.java.io/input-stream (.getBytes "{}")))

(defn- word->hash [word]
  (-> word
      (str ts)
      sha3/sha3-256))

(defn- anon-word [lookup word]
  (if (parser/ignore? word)
    word
    (let [root-word  (stemmer/stemming word)
          hashed-word (get @lookup root-word (word->hash root-word))]
      (swap! lookup assoc root-word hashed-word)
      hashed-word)))

(defn- sentence->anon-sentence [lookup line]
  (->> line
       tokeniser/txt->tokens
       (map (partial anon-word lookup))
       parser/words->sentence))

(defn from-chunk [chunk]
  (let [lookup (atom {})
        anon-sentences (->> chunk
                            parser/chunk->sentences
                            (map (partial sentence->anon-sentence lookup)))
        anon-chunk (parser/sentences->chunk anon-sentences)]
    {:lookup @lookup :content anon-chunk}))

(defn- anonomise-file [in-file out-file lookup-dict]
  (let [lookup (atom lookup-dict)]
    (with-open [reader (clojure.java.io/reader in-file)]
      (with-open [writer (clojure.java.io/writer out-file)]
        (doseq [line (line-seq reader)]
          (.write writer (sentence->anon-sentence lookup line)))))
    @lookup))

(defn- path [file] (.getParent (clojure.java.io/file file)))

(defn from-file
  ([in-file out-file whitelist-file]
   (from-file in-file out-file whitelist-file (empty-lookup-resource)))
  ([in-file out-file whitelist-file lookup-file]
   (let [old-lookup (whitelist/file->map lookup-file)
         lookup (anonomise-file in-file out-file old-lookup)
         lookup-whitelisted (whitelist/from-file lookup whitelist-file)
         out-dir (path out-file)]
     (spit (str out-dir "/lookup.edn") (prn-str lookup))
     (spit (str out-dir "/lookup.edn.whitelisted") (prn-str lookup-whitelisted)))))

(defn from-files
  ([in-dir out-dir whitelist-file]
   (from-files in-dir out-dir whitelist-file (empty-lookup-resource)))
  ([in-dir out-dir whitelist-file lookup-file]
   (let [old-lookup (whitelist/file->map lookup-file)
         new-lookup (->> (file-seq (clojure.java.io/file in-dir))
                         (remove #(.isDirectory %1))
                         (reduce (fn [lookup in-file]
                                   (let [out-file (clojure.string/replace in-file (re-pattern (str "^" in-dir)) out-dir)
                                         new-lookup (anonomise-file in-file out-file lookup)]
                                     (println (str "lookup:" (count new-lookup) " " in-file " -> " out-file))
                                     new-lookup))
                                 old-lookup))
         lookup-whitelisted (whitelist/from-file new-lookup whitelist-file)]
     (spit (str out-dir "/lookup.edn")             (prn-str new-lookup))
     (spit (str out-dir "/lookup.edn.whitelisted") (prn-str lookup-whitelisted)))))

(comment
  (from-files "test/fixtures" "/tmp" "test/fixtures/whitelist.txt"))
