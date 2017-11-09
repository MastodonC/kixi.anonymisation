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

(defn from-file [lookup-file whitelist-file]
  (let [lookup (-> lookup-file slurp clojure.edn/read-string)
        whitelist (slurp whitelist-file)
        whitelist-lookup  (filter-lookup lookup whitelist)]
    (spit (str lookup-file ".whitelisted") (prn-str whitelist-lookup))
    whitelist-lookup))