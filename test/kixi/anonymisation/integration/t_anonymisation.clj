(ns kixi.anonymisation.integration.t-anonymisation
  (:require [midje.sweet :refer :all]
            [kixi.anonymisation.recover :as recover]
            [kixi.anonymisation.hide :as hide]))

(def txt          "six impossible things before breakfast.")
(def stemmed-text "six imposs thing befor breakfast.")

(facts "with strings"
  (fact "it should recover anonymised content from the lookup"
    (let [{lookup :lookup content :content} (hide/from-chunk txt)]
      (recover/from-chunk lookup content) => stemmed-text))

  (fact "it should leave words not in the lookup as hashed"
    (let [{lookup :lookup content :content} (hide/from-chunk txt)
          incomplete-lookup (dissoc lookup "six" )]

      (-> (recover/from-chunk incomplete-lookup content)
          tokeniser/txt->tokens
          first
          ) =not=> "six"))
  )

(facts "with files"
  (fact "it should hide and recover all content from a master lookup"
    (spit "test/fixtures/in.txt" txt)
    (spit "test/fixtures/whitelist.txt" "six\n impossible\n things")

    (hide/from-file "test/fixtures/in.txt" "test/fixtures/out.txt" "test/fixtures/whitelist.txt")

    (let [recovered (recover/from-file "test/fixtures/lookup.edn" "test/fixtures/out.txt")]
      (slurp "test/fixtures/out.txt.recovered") => "six imposs thing befor breakfast."))

  (fact "it should hide and recover parital content from whitelisted lookup"
    (spit "test/fixtures/in.txt"         txt)
    (spit "test/fixtures/whitelist.txt" "six\n impossible\n things")

    (hide/from-file "test/fixtures/in.txt" "test/fixtures/out.txt" "test/fixtures/whitelist.txt")

    (let [recovered-whitelisted (recover/from-file "test/fixtures/lookup.edn.whitelisted" "test/fixtures/out.txt")
          partial-content (slurp "test/fixtures/out.txt.recovered")]

      partial-content => (contains "six")
      partial-content => (contains "imposs")
      partial-content => (contains "thing")

      partial-content =not=> (contains "befor")
      partial-content =not=> (contains "breakfast")))
  )