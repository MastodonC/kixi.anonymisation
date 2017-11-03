(ns kixi.tokeniser
  (:import [org.apache.lucene.analysis.standard StandardTokenizer StandardAnalyzer])
  (:import [org.tartarus.snowball.ext englishStemmer])
  (:import [org.apache.lucene.analysis.snowball SnowballFilter])
  (:import [org.tartarus.snowball.ext EnglishStemmer])
  (:import [java.io StringReader]))

(defonce standard-tokenizer (StandardTokenizer.))

(defn stemmed
  [tk]
  (SnowballFilter. tk (englishStemmer.)))

(defn token-stream [str]
  (doto standard-tokenizer
    (.setReader (StringReader. str))
    (.reset))
  standard-tokenizer)

(defn next-token [tk]
  (if (.incrementToken tk)
    (let [at (.addAttribute tk CharTermAttribute)]
      (.toString at))))

(defn tokens [tk]
  (loop [token (next-token tk)
         collected []]
    (if token
      (recur (next-token tk) (concat collected [token]))
      (do
        (.end tk)
        (.close tk)
        collected))))

(defn tokenize [words] (tokens (token-stream words)))