(ns kixi.anonymisation.whitelist
  (:require [kixi.anonymisation.stemmer :as stemmer]
            [kixi.anonymisation.tokeniser :as tokeniser]))

(defn scrub [word]
  (->> word
       clojure.string/trim
       tokeniser/word->token
       stemmer/stemming))

(defn filter-lookup [lookup raw-whitelist]
  (let [whitelist  (->> raw-whitelist
                        clojure.string/split-lines
                        (map scrub))]
    (select-keys lookup whitelist)))

(defn from-file [lookup whitelist-file]
  (let [whitelist (slurp whitelist-file)]
    (if (seq whitelist)
      (filter-lookup lookup whitelist)
      lookup)))

(defn file->map [file]
  (-> file
      slurp
      clojure.edn/read-string))
