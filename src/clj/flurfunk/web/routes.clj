(ns flurfunk.web.routes
  (:use compojure.core
        ring.util.servlet)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [flurfunk.web.views :as views]
            [ring.util.response :as response]
            [clj-http.client :as http-client]))

(def ^:private server-uri (System/getProperty "flurfunk.server"))

(defn- make-proxy-uri [uri context scheme host port]
  (let [context-path (if context (.getContextPath context) "")
        path (.substring uri (count (str context-path "/proxy")))]
    (str (or server-uri
             (if (not (empty? context-path))
               (str (name scheme) "://" host ":" port "/flurfunk-server")
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
  (ANY "/proxy/*" {uri :uri
                   params :params
                   body :body
                   context :servlet-context
                   method :request-method
                   scheme :scheme
                   host :server-name
                   port :server-port}
       (if (= method :get)
         (http-client/get (make-proxy-uri uri context scheme host port)
                          {:query-params params})
         (http-client/post (make-proxy-uri uri context scheme host port)
                           {:body (slurp body)})))
  (route/resources "/")
  (route/resources "/mobile")
  (route/not-found "Page not found"))

(def flurfunk-web
     (handler/site main-routes))
