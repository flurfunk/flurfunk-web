(ns flurfunk.web.views
  (:use [hiccup core page-helpers]))

(defn index []
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content (str "width=device-width,initial-scale=1.0,"
                          "maximum-scale=1.0,user-scalable=0")}]
    [:title "Flurfunk"]
    (include-css "flurfunk.css")
    [:link {:rel "stylesheet" :type "text/css" :href "flurfunk-mobile.css"
            :media "only screen and (max-device-width: 480px)"}]]
   [:body
    [:script "
// Use a server on the same host, Jetty or Tomcat.
var flurfunkServer = location.href.replace(\"index.html\", \"\")
    .replace(\"flurfunk-web\", \"flurfunk-server\").replace(/\\/$/, \"\");
"]
    (include-js "flurfunk-web.js")]))

(defn index-dev []
  ;; TODO: Use hiccup
  (slurp "resources/public/index-dev.html"))
