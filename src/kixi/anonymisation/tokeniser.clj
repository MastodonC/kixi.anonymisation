(ns kixi.anonymisation.tokeniser
  (:require [opennlp.nlp :as nlp]))

(def tokenize   (nlp/make-tokenizer "resources/models/en-token.bin"))

(defn txt->tokens [str] (tokenize str))
(defn word->token [str] (first (tokenize str)))