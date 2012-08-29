(ns flurfunk.web.jetty
  "Runs Flurfunk in Jetty"
  (:require [ring.adapter.jetty :as jetty]
            [flurfunk.web.routes :as routes])
  (:gen-class))

(def default-port 8080)

(defn- get-port []
  (if-let [port (System/getProperty "flurfunk.port")]
    port
    (do (println (str "Using default port " default-port
                      ". Set the system property \"flurfunk.port\" "
                      "if you want a specific one."))
        default-port)))

(defn- load-props [file-name]
    (with-open [^java.io.Reader reader (clojure.java.io/reader (clojure.java.io/resource file-name))]
          (let [props (java.util.Properties.)]
                  (.load props reader)
                  (into {} (for [[k v] props] [(keyword k) (str v)])))))

(defn- get-version []
  (let [meta-file "META-INF/maven/de.viaboxx.flurfunk/flurfunk-web/pom.properties"]
    (if-let [props (load-props meta-file)]
             (:version props)
             "unknown")))

(defn- print-version []
  (println (str
             "==========================\n"
             "Flurfunk Web\n"
             "==========================\n"
             "http://flurfunk.github.com\n"
             "==========================\n"
             "Starting flurfunk-web:" (get-version))))

(defn -main [& args]
  (let [port (get-port)]
    (do (print-version)
      (try (jetty/run-jetty routes/app {:port (Integer. port)})
        (catch NumberFormatException e
          (println (str "Invalid port number: '" port "'"))
          (System/exit 1))))))
