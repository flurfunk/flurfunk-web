(ns flurfunk-web.routes
  ""
  (:use compojure.core
        ring.util.servlet)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]))

(defroutes main-routes
  (GET "/" {uri :uri}
       {:status 302 :headers {"Location" (str uri
                                              (if (not (.endsWith uri "/")) "/")
                                              "index.html")}})
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
     (handler/site main-routes))
