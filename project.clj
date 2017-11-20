(defproject kixi.anonymisation "0.1.0-SNAPSHOT"
  :description "Anonymisation"
  :url "https://github.com/MastodonC/kixi.anonymisation"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]

                 [cc.mallet/mallet "2.0.8"]
                 [clojure-opennlp "0.4.0"]
                 [com.github.rholder/snowball-stemmer "1.3.0.581.1"]
                 [org.bouncycastle/bcprov-jdk15on "1.58"]
                 [pandect "0.6.1"]
                 ]

  :profiles {:dev {:dependencies [[midje "1.8.3"]]
                   :plugins      [[lein-kibit "0.1.5"]
                                  [lein-midje "3.2.1"]
                                  [jonase/eastwood "0.2.5"]]}})
