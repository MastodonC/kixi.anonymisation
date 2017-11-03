(ns kixi.tokeniser
  (:import [org.apache.lucene.analysis.standard StandardTokenizer StandardAnalyzer])
  (:import [org.apache.lucene.analysis.snowball SnowballFilter])
  (:import [org.apache.lucene.analysis.tokenattributes CharTermAttribute])
  (:import [org.tartarus.snowball.ext englishStemmer])
  (:import [java.io StringReader]))

(defonce standard-tokenizer (StandardTokenizer.))

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

(defn line->tokens [words] (tokens (token-stream words)))