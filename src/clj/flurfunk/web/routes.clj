(ns flurfunk.web.routes
  (:use compojure.core
        ring.util.servlet)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [flurfunk.web.views :as views]
            [ring.util.response :as response]
            [clj-http.client :as http-client]))

;; TODO: What should happen when this property has not been set?
(def ^:private server-uri (System/getProperty "flurfunk.server.uri"))

(defn- make-proxy-uri [uri]
  (let [path (.substring uri (count "/proxy"))]
    (str server-uri path)))

(defroutes main-routes
  (GET "/" {uri :uri
            request :servlet-context}
       (if (and request (= uri (.getContextPath request)))
         (response/redirect (str uri "/"))
         (views/index false)))
  (GET "/mobile" [] (views/index true))
  (GET "/dev" [] (views/index-dev false))
  (GET "/mobile/dev" [] (views/index-dev true))
  (GET "/proxy/*" {uri :uri
                   params :params}
       (http-client/get (make-proxy-uri uri) {:query-params params}))
  (POST "/proxy/*" {uri :uri
                    body :body}
        (http-client/post (make-proxy-uri uri) {:body (slurp body)}))
  (route/resources "/")
  (route/resources "/mobile")
  (route/not-found "Page not found"))

(def flurfunk-web
     (handler/site main-routes))
