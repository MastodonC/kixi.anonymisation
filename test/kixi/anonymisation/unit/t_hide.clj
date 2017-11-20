(ns kixi.anonymisation.unit.t-hide
  (:require [midje.sweet :refer :all]
            [kixi.anonymisation.tokeniser :as tokeniser]
            [kixi.anonymisation.parser :as parser]
            [kixi.anonymisation.hide :refer :all]))

(fact "it should convert each word to a hash"
  (let [hashed (:content (from-chunk "Curiouser and curiouser!"))
        hashed-words (tokeniser/txt->tokens hashed)]
    (count hashed-words) => 4

    hashed =not=> (contains #"(?i)Curiouser")
    hashed =not=> (contains #"(?i)and")
    hashed =not=> (contains #"(?i)curiouser!"))
  )

(fact "it should use the same hash for the same stemmed word"
  (let [hashes (-> (from-chunk "Feeds a feed")
                   :content
                   tokeniser/txt->tokens)]
    (nth hashes 0) => (nth hashes 2))
  )

(fact "punctuation should not affect word hashing"
  (let [hashes (-> (from-chunk "Feeds a feed.")
                   :content
                   tokeniser/txt->tokens)]
    (nth hashes 0) => (nth hashes 2))
  )

(fact "Sentence structure should be preserved"
  (let [hashes (-> (from-chunk "Feeds a feed. Or not.")
                   :content
                   tokeniser/txt->tokens)]
    (nth hashes 3) => "."
    (nth hashes 6) => "."
    )
  )
