(ns kixi.anonymisation.unit.t-recover
  (:require [midje.sweet :refer :all]
            [kixi.anonymisation.recover :as recover]
            [kixi.anonymisation.hide :as hide]))

(fact "it should recover anonymised content from the lookup"
  (let [txt "Why, sometimes I've believed as many as six impossible things before breakfast\n"
        {lookup :lookup content :content} (hide/anonymise-chunk txt)]

    (recover/from-chunk lookup txt) => txt))
