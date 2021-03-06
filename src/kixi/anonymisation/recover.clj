(ns kixi.anonymisation.recover
  (:require [kixi.anonymisation.parser :as parser]
            [kixi.anonymisation.tokeniser :as tokeniser]))

(defn word->recovered-word [lookup word]
  (let [w (get lookup word word)]
    (when (and (= w word) (>= (count w) 32)) (println :failed w))
    w))

(defn line->recovered-line [lookup line]
  (->> line
       tokeniser/txt->tokens
       (map (partial word->recovered-word lookup))
       parser/words->sentence))


(defn from-paragraphs [txt lookup]
  (let [reverse-lookup (clojure.set/map-invert lookup)]
    (->> txt
         parser/chunk->sentences
         (map (partial line->recovered-line reverse-lookup))
         (parser/sentences->chunk))))

(defn from-chunk  [txt lookup]
  (let [paragraphs (clojure.string/split txt #"\n{2,}")
        recovered-paragraphs (map #(from-paragraphs %1 lookup) paragraphs)]
    (clojure.string/join "\n\n" recovered-paragraphs)))

(defn from-file [in-file out-file lookup-file]
  (let [lookup (-> lookup-file
                   slurp
                   clojure.edn/read-string)
        chunk (slurp in-file)
        recovered-chunk (from-chunk chunk lookup)]
    (spit out-file recovered-chunk)))

(defn from-files [in-dir out-dir lookup-file]
  (let [all-files (->> in-dir
                       clojure.java.io/file
                       file-seq
                       (remove #(.isDirectory %1))
                       (remove #(= (first (.getName %1)) \.)))]
    (doseq [in-file all-files]
      (let [out-file (clojure.string/replace in-file (re-pattern (str "^" in-dir)) out-dir)]
        (println (.getPath in-file) " -> " out-file)
        (clojure.java.io/make-parents out-file)
        (from-file in-file out-file lookup-file)))))