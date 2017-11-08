(ns kixi.anonymisation.unit.t-hide
  (:require [midje.sweet :refer :all]
            [kixi.anonymisation.tokeniser :as tokeniser]
            [kixi.anonymisation.parser :as parser]
            [kixi.anonymisation.hide :refer :all]))

(defn- words-without-punctation [s] (clojure.string/split s #"\s+|\."))
(defn- words-with-punctation    [s] (clojure.string/split s #"\s+"))

(fact "it should convert each word to a hash"
  (let [hashed (:content (anonymise-chunk "Curiouser and curiouser!"))
        hashed-words (words-without-punctation hashed)]

    (count hashed-words) => 3

    hashed =not=> (contains #"(?i)Curiouser")
    hashed =not=> (contains #"(?i)and")
    hashed =not=> (contains #"(?i)curiouser!"))
  )

(fact "it should use the same hash for the same stemmed word"
  (let [hashes (-> (anonymise-chunk "Feeds a feed")
                   :content
                   words-without-punctation)]
    (nth hashes 0) => (nth hashes 2))
  )

(fact "punctation should not affect word hashing"
  (let [hashes (-> (anonymise-chunk "Feeds a feed.")
                   :content
                   words-without-punctation)]
    (nth hashes 0) => (nth hashes 2))
  )

(fact "Sentence structure should be preserved"
  (let [hashes (-> (anonymise-chunk "Feeds a feed. Or not.")
                   :content
                   words-with-punctation) ]
    (nth hashes 2) => (has-suffix ".")
    (nth hashes 4) => (has-suffix "."))
  )
