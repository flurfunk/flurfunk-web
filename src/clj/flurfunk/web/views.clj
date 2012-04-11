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
var flurfunkServer = location.href.replace(\"index.html\", \"\")
    .replace(/\\/$/, \"\") + \"/proxy\";
"]
   (include-js "flurfunk.js")))

(defn index-dev [mobile?]
  (index-template mobile?
   [:form
    [:input {:type "checkbox" :id "use-real-server"
             :onchange "enableUrlInput(this.checked)"}]
    [:label {:for "use-real-server"} "Use real server"]
    [:label {:for "server-url"} "URL:"]
    [:input {:id "server-url" :type "text"}]
    [:button {:onclick "reloadPage()"} "Reload"]]
   [:hr]
   [:script "
function enableUrlInput(enable) {
    document.getElementById(\"server-url\").disabled = !enable;
}

function reloadPage() {
    var url = location.href,
        parametersIndex = url.indexOf(\"#\");
    if (parametersIndex != -1)
        url = url.substring(0, parametersIndex);
    if (document.getElementById(\"use-real-server\").checked)
        url += \"#server=\" + document.getElementById(\"server-url\").value;
    window.location.href = url;
}

function getParameter(name) {
    var parameters = location.hash.substring(1).split(\"&\");
    for (var i = 0; i < parameters.length; i++) {
        var pair = parameters[i].split(\"=\");
        if (pair[0] == name)
            return pair[1];
    }
    return null;
}

// The Flurfunk server's URL. The stub server is used when it is null.
var flurfunkServer = getParameter(\"server\");
if (flurfunkServer) {
    document.getElementById(\"server-url\").value = flurfunkServer;
    document.getElementById(\"use-real-server\").checked = true;
} else
    enableUrlInput(false);
"]
   (include-js "flurfunk-dev.js")))
