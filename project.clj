(defproject de.viaboxx.flurfunk/flurfunk-web "0.1.0-SNAPSHOT"
  :description "Web interface for Flurfunk"
  :source-paths ["src/clj"]
  :cljs-source-path "src/cljs"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/clojurescript "0.0-993"]
                 [compojure "1.0.2"]
                 [hiccup "0.3.8"]
                 [ring/ring-jetty-adapter "0.3.11"]]
  :plugins [[lein-cljsbuild "0.1.6"]
            [lein-ring "0.6.3"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild
  {:builds [{:source-path "src/cljs"
             :compiler {:output-to "resources/public/flurfunk.js"
                        :optimizations :advanced}}
            {:source-path "src/cljs"
             :compiler {:output-to "resources/public/flurfunk-dev.js"
                        :pretty-print true}}]}
  :ring {:handler flurfunk.web.routes/flurfunk-web}
  :main flurfunk.web.jetty)
