(ns kixi.unit.t-anonoymisation
  (:require [midje.sweet :refer :all]
            [kixi.anonymisation :refer :all]))

(fact "it should convert each word to a hash"
  (let [hashed (anonymise-chunk "Curiouser and curiouser!")
        hashed-words (clojure.string/split hashed #"\s")]

    (count hashed-words) => 3

    (re-seq #"(?i)Curiouser" hashed) => nil
    (re-seq #"(?i)and" hashed) => nil
    (re-seq #"(?i)curiouser!" hashed) => nil))
