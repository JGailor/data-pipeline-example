(defproject persister "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.novemberain/langohr "3.6.1"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-time "0.12.0"]
                 [com.layerware/hugsql "0.4.7"]
                 [org.postgresql/postgresql "9.4.1207"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.1"]
                 [clojurewerkz/elastisch "2.2.1"]]
  :main ^:skip-aot persister.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
