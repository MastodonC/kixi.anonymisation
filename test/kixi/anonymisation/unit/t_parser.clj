(ns kixi.anonymisation.unit.t-parser
  (:require [midje.sweet :refer :all]
            [kixi.anonymisation.parser :refer :all]))

(fact "it should convert a single chunk of text into sentences"
  (chunk->sentences "A or. B by. Why?") => ["A or" "B by" "Why?"]
  )

(fact "it should convert sentences back into a single chunk of sentences"
  (sentences->chunk ["12"]) => "12."
  (sentences->chunk ["12" "34"]) => "12. 34.")