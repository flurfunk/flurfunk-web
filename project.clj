(defproject de.viaboxx.flurfunk/flurfunk-web "0.1.0-SNAPSHOT"
  :description "Web interface for Flurfunk"
  :source-path "src/clj"
  :cljs-source-path "src/cljs"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [compojure/compojure "0.6.5"]
                 [ring/ring-jetty-adapter "0.3.11"]]
  :dev-dependencies [[lein-ring "0.4.5"]]
  :plugins [[lein-cljsbuild "0.1.3"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild
  {:builds [{:source-path "src/cljs"
             :compiler {:output-to "resources/public/flurfunk-web.js"
                        :optimizations :advanced}}
            {:source-path "src/cljs"
             :compiler {:output-to "resources/public/flurfunk-web-dev.js"
                        :pretty-print true}}]}
  :ring {:handler flurfunk.web.routes/flurfunk-web}
  :main flurfunk.web.jetty
  :repositories
  {"releases"
   {:url "https://www.viaboxxsystems.de/nexus/content/groups/public/"}
   "snapshots"
   {:url "https://www.viaboxxsystems.de/nexus/content/groups/public/"}})