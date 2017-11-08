(ns kixi.anonymisation.integration.t-anonymisation
  (:require [midje.sweet :refer :all]
            [kixi.anonymisation.parser :as parser]
            [kixi.anonymisation.recover :as recover]
            [kixi.anonymisation.hide :as hide]))

(def txt          "six impossible things before breakfast.")
(def stemmed-text "six imposs thing befor breakfast.")

(fact "it should recover anonymised content from the lookup"
  (let [{lookup :lookup content :content} (hide/from-chunk txt)]
    (recover/from-chunk lookup content) => stemmed-text))

(fact "it should leave words not in the lookup as hashed"
  (let [{lookup :lookup content :content} (hide/from-chunk txt)
        incomplete-lookup (dissoc lookup "six" )]

    (-> (recover/from-chunk incomplete-lookup content)
        parser/sentence->words
        first
        ) =not=> "six"))
