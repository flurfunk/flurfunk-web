(ns flurfunk.client
  (:require [goog.dom :as dom]
            [goog.dom.xml :as xml]
            [goog.net.XhrIo :as XhrIo]))

(defprotocol Client
  (client-get-messages [this callback])
  (client-send-message [this message callback]))

(deftype StubClient [messages] Client
  (client-get-messages [this callback] (callback @messages))

  (client-send-message
   [this message callback]
   (swap! messages (fn [messages]
                     (cons (conj message {:id (str (count messages))
                                          :timestamp (. (js/Date.) (getTime))})
                           messages)))
   (callback)))

(defn- get-request [uri callback]
  (XhrIo/send uri callback))

(defn- wrap-context [uri]
  (let [pathname (.pathname js/location)]
    (str pathname
         (if (not (= "/" (nth pathname (- 1 (.length pathname))))) "/")
         uri)))

(defn- unmarshal-messages [messages]
  (let [xml (xml/loadXml messages)
        message-tags (dom/getChildren (.firstChild xml))]
    (map (fn [message-tag]
           (let [text (.textContent message-tag)
                 id (. message-tag (getAttribute "id"))
                 author (. message-tag (getAttribute "author"))
                 timestamp (js/parseInt (. message-tag
                                           (getAttribute "timestamp")))]
             {:id id :author author :timestamp timestamp :text text}))
         message-tags)))

(defn- post-request [uri callback content]
  (XhrIo/send uri (fn [e] (callback)) "post", content))

(defn- marshal-message [message]
  (str "<message author='" (:author message) "'>" (:text message) "</message>"))

(deftype HttpClient [] Client
  (client-get-messages
   [this callback]
   (get-request (wrap-context "messages")
                (fn [e]
                  (let [target (.target e)
                        text (. target (getResponseText))]
                    (callback (unmarshal-messages text))))))

  (client-send-message
   [this message callback]
   (post-request (wrap-context "message")
                 (fn [] (callback)) (marshal-message message))))

(defn- make-client []
  (if js/flurfunkStubClient
    (StubClient. (atom []))
    (HttpClient.)))

(def ^{:private true} client (make-client))

(defn get-messages [callback]
  (client-get-messages client callback))

(defn send-message [message callback]
  (client-send-message client message callback))