(ns flurfunk.web.views
  (:use [hiccup core page-helpers]))

(defn- index-template [mobile? & body]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content (str "width=device-width,initial-scale=1.0,"
                          "maximum-scale=1.0,user-scalable=0")}]
    [:title "Flurfunk"]
    (if mobile?
      (include-css "flurfunk-mobile.css")
      (include-css "flurfunk.css"))]
   (vec (cons :body body))))

(defn index [mobile?]
  (index-template mobile?
   [:script "
var flurfunkServer = location.href.replace(/\\/$/, '/proxy');
"]
   (include-js "flurfunk.js")))

(defn index-dev [mobile?]
  (index-template mobile?
   [:form
    [:input {:type "checkbox" :id "use-real-server"
             :onchange "enableUrlInput(this.checked)"}]
    [:label {:for "use-real-server"} "Use real server"]]
   [:hr]
   [:script "
var flurfunkServer;

(function() {
    var baseUrl = location.origin + location.pathname,
        useRealServer = document.getElementById('use-real-server');

    useRealServer.checked = location.hash === '#use-real-server';

    flurfunkServer = useRealServer.checked ?
        baseUrl.replace(/\\/dev$/, '/proxy') : null;

    useRealServer.onchange = function() {
        location.hash = useRealServer.checked ? '#use-real-server' : '';
        location.reload();
    };
})();
"]
   (include-js "flurfunk-dev.js")))
