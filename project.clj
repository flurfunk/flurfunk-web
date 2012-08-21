(defproject de.viaboxx.flurfunk/flurfunk-web "1.0"
  :description "Web interface for Flurfunk"
  :source-paths ["src/clj"]
  :cljs-source-path "src/cljs"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/clojurescript "0.0-993"]
                 [compojure "1.0.2"]
                 [hiccup "0.3.8"]
                 [ring/ring-jetty-adapter "0.3.11"]
                 [clj-http "0.3.6"]]
  :plugins [[lein-cljsbuild "0.1.6"]
            [lein-ring "0.6.5"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild
  {:builds {:prod
            {:source-path "src/cljs"
             :compiler {:output-to "resources/public/flurfunk.js"
                        :optimizations :advanced}}
            :dev
            {:source-path "src/cljs"
             :compiler {:output-to "resources/public/flurfunk-dev.js"
                        :pretty-print true}}}}
  :ring {:handler flurfunk.web.routes/app}
  :main flurfunk.web.jetty
  :uberjar-name "flurfunk-web.jar")
