(ns flurfunk.web.views
  (:use [hiccup core page-helpers]))

(defn index []
  (html5
   [:head
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
