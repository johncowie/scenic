(defproject scenic "0.2.2-SNAPSHOT"
  :description "Bidi wrapper for pretty text file routes"
  :url "https://github.com/johncowie/scenic"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [bidi "1.10.5"]
                 ]
  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [ring-mock "0.1.5"]]
                     :plugins [[lein-midje "3.1.1"]]}})
