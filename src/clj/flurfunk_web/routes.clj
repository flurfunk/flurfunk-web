(ns flurfunk-web.routes
  (:use compojure.core
        ring.util.servlet)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :as response]))

(defroutes main-routes
  (GET "/" [] (response/content-type
               (response/resource-response "public/index.html") "text/html"))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
     (handler/site main-routes))
