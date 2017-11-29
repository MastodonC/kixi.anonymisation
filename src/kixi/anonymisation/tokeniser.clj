(ns kixi.anonymisation.tokeniser
  (:require [opennlp.nlp :as nlp]))

(defonce tokenize
  (nlp/make-tokenizer
   (clojure.java.io/resource "en-token.bin")))

(defn txt->tokens [str] (tokenize str))
(defn word->token [str] (first (tokenize str)))