(ns kixi.anonymisation.recover
  (:require [kixi.anonymisation.parser :as parser]
            [kixi.anonymisation.tokeniser :as tokeniser]))

(defn word->recovered-word [lookup word]
  (get lookup word word))

(defn line->recovered-line [lookup line]
  (->> line
       tokeniser/txt->tokens
       (map (partial word->recovered-word lookup))
       parser/words->sentence))

(defn from-chunk  [txt lookup]
  (let [reverse-lookup (clojure.set/map-invert lookup)]
    (->> txt
         parser/chunk->sentences
         (map (partial line->recovered-line reverse-lookup))
         (parser/sentences->chunk))))

(defn from-file [file lookup-file]
  (let [lookup (-> lookup-file
                   slurp
                   clojure.edn/read-string)
        chunk (slurp file)
        recovered-chunk (from-chunk chunk lookup)]
    (spit (str file ".recovered") recovered-chunk)))

(defn from-files [in-dir lookup-file]
  (let [all-files (->> in-dir
                       clojure.java.io/file
                       file-seq
                       (remove #(.isDirectory %1)))]
    (doseq [file all-files]
      (from-file file lookup-file))))
