(ns flurfunk.web.routes
  (:use compojure.core
        ring.util.servlet)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :as response]))

(defroutes main-routes
  (GET "/" {uri :uri
            request :servlet-context}
       (if (and request (= uri (.getContextPath request)))
         (response/redirect (str uri "/"))
         (response/content-type
          (response/resource-response "public/index.html") "text/html")))
  (route/resources "/")
  (route/not-found "Page not found"))

(def flurfunk-web
     (handler/site main-routes))
