(ns kixi.anonymisation.integration.t-anonymisation
  (:require [midje.sweet :refer :all]
            [kixi.anonymisation.parser :as parser]
            [kixi.anonymisation.recover :as recover]
            [kixi.anonymisation.hide :as hide]))

(fact "it should recover anonymised content from the lookup"
  (let [txt "six imposs thing befor breakfast."
        {lookup :lookup content :content} (hide/anonymise-chunk txt)]

    (recover/from-chunk lookup content) => txt))

(fact "it should leave words not in the lookup as hashed"
  (let [txt "six imposs thing befor breakfast."
        {lookup :lookup content :content} (hide/anonymise-chunk txt)
        incomplete-lookup (dissoc lookup "six" )]

    (-> (recover/from-chunk incomplete-lookup content)
        parser/sentence->words
        first
        ) =not=> "six"))