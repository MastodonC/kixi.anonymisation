(ns kixi.anonymisation.parser
  (:require
   [opennlp.nlp :as nlp]))

(defonce txt->sentences
  (nlp/make-sentence-detector
   (clojure.java.io/resource "en-sent.bin")))

(def ignore-words ["?" "!" "." "," ":" ";"])
(defn ignore? [w] (some #{w} ignore-words))

(defn chunk->sentences [chunk] (txt->sentences chunk))

(defn sentences->chunk [lines]
  (->> lines
       clojure.string/join
       clojure.string/trim))

(defn words->sentence [words]
  (reduce (fn [sentence word]
            (if (ignore? word)
              (str sentence word)
              (str sentence " " word)))
          ""
          words))
