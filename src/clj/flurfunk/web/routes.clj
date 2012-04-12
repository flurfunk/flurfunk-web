(ns flurfunk.web.routes
  (:use compojure.core
        ring.util.servlet)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [flurfunk.web.views :as views]
            [ring.util.response :as response]
            [clj-http.client :as http-client]))

(def ^:private server-uri (System/getProperty "flurfunk.server"))

(defn- make-proxy-uri [uri context]
  (let [path (second (re-find #"/proxy(.*)" uri))]
    (str (or server-uri
             (if (and context (not (empty? (.getContextPath context))))
               "http://localhost:8080/flurfunk-server"
               "http://localhost:4000"))
         path)))

(defroutes main-routes
  (GET "/" {uri :uri
            context :servlet-context}
       (if (and context (= uri (.getContextPath context)))
         (response/redirect (str uri "/"))
         (views/index false)))
  (GET "/mobile" [] (views/index true))
  (GET "/dev" [] (views/index-dev false))
  (GET "/mobile/dev" [] (views/index-dev true))
  (ANY "*/proxy/*" {uri :uri
                   context :servlet-context
                   method :request-method
                   params :params
                   body :body}
       (let [proxy-uri (make-proxy-uri uri context)]
         (:body (if (= method :get)
                  (http-client/get proxy-uri {:query-params params})
                  (http-client/post proxy-uri {:body (slurp body)})))))
  (route/resources "/")
  (route/resources "/mobile")
  (route/not-found "Page not found"))

(def flurfunk-web
     (handler/site main-routes))
