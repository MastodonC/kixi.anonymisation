(ns kixi.unit.t-anonoymisation
  (:require [midje.sweet :refer :all]
            [kixi.anonymisation :refer :all]))

(fact "it should convert each word to a hash"
  (let [hashed (anonymise-chunk "Curiouser and curiouser!")
        hashed-words (line->tokens hashed)]

    (count hashed-words) => 3

    (re-seq #"(?i)Curiouser" hashed) => nil
    (re-seq #"(?i)and" hashed) => nil
    (re-seq #"(?i)curiouser!" hashed) => nil))

(fact "it should use the same hash for the same stemmed word"
  (let [hashes (line->words (anonymise-chunk "Feeds a feed"))]
    (nth hashes 0) => (nth hashes 2))
  )

(future-fact "punctation should not affect word hashing"
  (let [hashes (line->words (anonymise-chunk "Feeds a feed."))]
    (nth hashes 0) => (nth hashes 2))
  )

(future-fact "punctation should be preserved"
  (let [hashes (line->words (anonymise-chunk "Feeds a feed."))]
    (last hashes) => ".")
  )