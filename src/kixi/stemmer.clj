(ns kixi.stemmer
  (import [org.tartarus.snowball.ext englishStemmer]))

(def stemmer (englishStemmer.))

(defn- token->stemed-token [token]
  (-> stemmer
      (doto (.setCurrent (clojure.string/lower-case token)) (.stem))
      (.getCurrent)))

(defn stemming [token] (token->stemed-token token))
