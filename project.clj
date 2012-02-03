(defproject de.viaboxx.flurfunk/flurfunk-web "0.1.0-SNAPSHOT"
  :description "Web interface for Flurfunk"
  :source-path "src/clj"
  :cljs-source-path "src/cljs"
  :dependencies [[lein-clojurescript "1.0.0-SNAPSHOT"]
                 [compojure/compojure "0.6.5"]]
  :dev-dependencies [[lein-ring "0.4.5"]]
  :ring {:handler flurfunk-web.routes/app}
  :repositories
  {"releases"
   {:url "https://www.viaboxxsystems.de/nexus/content/groups/public/"}
   "snapshots"
   {:url "https://www.viaboxxsystems.de/nexus/content/groups/public/"}})