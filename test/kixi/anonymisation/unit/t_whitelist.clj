(ns kixi.anonymisation.unit.t-whitelist
  (:require [midje.sweet :refer :all]
            [kixi.anonymisation.whitelist :refer :all]))

(fact "it should remove words that do not appear in the whitelist"
  (filter-lookup {"alic"  "11111"
                  "in"     "22222"
                  "wonder" "33333"
                  "land"   "44444"}
                 "Alice\n Wonder") => {"alic" "11111" "wonder" "33333"}
  )
