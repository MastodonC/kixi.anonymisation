(ns kixi.unit.t-recover
  (:require [midje.sweet :refer :all]
            [kixi.recover :as recover]
            [kixi.anonymisation :as anon]))

(fact "it should recover anonymised content from the lookup"
  (let [txt "Why, sometimes I've believed as many as six impossible things before breakfast\n"
        {lookup :lookup content :content} (anon/anonymise-chunk txt)]

    (recover/from-chunk lookup txt) => txt))
