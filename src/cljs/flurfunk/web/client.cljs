(ns flurfunk.web.client
  (:require [flurfunk.web.dom-helpers :as dom]
            [goog.dom.xml :as xml]
            [goog.net.XhrIo :as XhrIo]))

(defprotocol Client
  (client-get-messages [this callback] [this callback params])
  (client-send-message [this message callback]))

(deftype StubClient [messages] Client
  (client-get-messages [this callback] (callback @messages))

  (client-get-messages
   [this callback params]
   (callback (filter (fn [message]
                       (and
                        (if-let [since (:since params)]
                          (> (:timestamp message) since)
                          true)
                        (if-let [before (:before params)]
                          (< (:timestamp message) before)
                          true)))
                     @messages)))

  (client-send-message
   [this message callback]
   (swap! messages (fn [messages]
                     (cons (conj message {:id (str (count messages))
                                          :timestamp (.getTime (js/Date.))})
                           messages)))
   (callback)))

(defn- get-request [uri callback]
  (XhrIo/send uri callback))

(defn- make-uri [uri server]
  (str server
       (if (not (= "/" (nth server (- 1 (.-length server))))) "/")
       uri))

(defn- unmarshal-messages [messages]
  (let [xml (xml/loadXml messages)
        message-tags (dom/get-children (.-firstChild xml))]
    (map (fn [message-tag]
           (let [text (.-textContent message-tag)
                 id (.getAttribute message-tag "id")
                 author (.getAttribute message-tag "author")
                 timestamp (js/parseInt (.getAttribute message-tag
                                                       "timestamp"))]
             {:id id :author author :timestamp timestamp :text text}))
         message-tags)))

(defn- post-request [uri callback content]
  (XhrIo/send uri (fn [e] (callback)) "post" content))

(defn- marshal-message [message]
  (let [channels-str (reduce #(str %1 "," %2) (:channels message))]
    (str "<message author='" (:author message) "' channels='"
         channels-str "'>" (:text message) "</message>")))

(defn- build-query-string [params]
  (if (empty? params)
    ""
    (str "?"
         (reduce #(str %1 "&" %2)
                 (map #(str (name %) "=" (% params)) (keys params))))))

(deftype HttpClient [server] Client
  (client-get-messages
   [this callback]
   (client-get-messages this callback {}))

  (client-get-messages
   [this callback params]
   (get-request (make-uri (str "messages" (build-query-string params)) server)
                (fn [e]
                  (let [target (.-target e)
                        text (.getResponseText target)]
                    (callback (unmarshal-messages text))))))
  (client-send-message
   [this message callback]
   (post-request (make-uri "message" server)
                 (fn [] (callback)) (marshal-message message))))

(defn- make-client []
  (if-let [server js/flurfunkServer]
    (HttpClient. server)
    (StubClient. (atom []))))

(def ^{:private true} client (make-client))

(defn get-messages
  ([callback]
     (client-get-messages client callback))
  ([callback since]
     (client-get-messages client callback since)))

(defn send-message [message callback]
  (client-send-message client message callback))