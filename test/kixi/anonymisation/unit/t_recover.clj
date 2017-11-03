(ns kixi.anonymisation.unit.t-recover
  (:require [midje.sweet :refer :all]
            [kixi.anonymisation.recover :as recover]
            [kixi.anonymisation.hide :as hide]))

(fact "it should recover anonymised content from the lookup"
  (let [txt "six imposs thing befor breakfast\n"
        {lookup :lookup content :content} (hide/anonymise-chunk txt)]

    (recover/from-chunk lookup content) => txt))


(fact "it should leave words not in the lookup as hashed"
  (let [txt "six imposs thing befor breakfast"
        {lookup :lookup content :content} (hide/anonymise-chunk txt)
        incomplete-lookup (dissoc lookup "six" )]

    (-> (recover/from-chunk incomplete-lookup content)
        hide/line->words
        first
        ) =not=> "six"))