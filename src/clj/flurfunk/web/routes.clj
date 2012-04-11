(ns flurfunk.web.routes
  (:use compojure.core
        ring.util.servlet)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [flurfunk.web.views :as views]
            [ring.util.response :as response]
            [clj-http.client :as http-client]))

(def ^:private server-uri (System/getProperty "flurfunk.server"))

(defn- make-proxy-uri [uri request]
  (let [context-path (if request (.getContextPath request) "")
        path (.substring uri (count (str context-path "/proxy")))]
    (str (or server-uri
             (if (not (empty? context-path))
               ;; TODO: Don't hard code host and port
               "http://localhost:8080/flurfunk-server"
               "http://localhost:4000"))
         path)))

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
                   params :params
                   request :servlet-context}
       (http-client/get (make-proxy-uri uri request) {:query-params params}))
  (POST "/proxy/*" {uri :uri
                    body :body
                    request :servlet-context}
        (http-client/post (make-proxy-uri uri request) {:body (slurp body)}))
  (route/resources "/")
  (route/resources "/mobile")
  (route/not-found "Page not found"))

(def flurfunk-web
     (handler/site main-routes))
