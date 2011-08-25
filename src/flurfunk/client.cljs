(ns flurfunk.client
  (:require [goog.dom :as dom]
            [goog.dom.xml :as xml]
            [goog.net.XhrIo :as XhrIo]))

;; TODO: Private
(def stub-messages [])

(defn- get-messages-stub [callback]
  (callback stub-messages))

(defn- send-message-stub [message callback]
  (def stub-messages (cons (conj message {:id (. (js/Date.) (getTime))})
                           stub-messages))
  (callback))

(defn- get-request [uri callback]
  (XhrIo/send uri callback))

(defn- unmarshal-messages [messages]
  (let [xml (xml/loadXml messages)
        message-tags (dom/getChildren (.firstChild xml))]
    (map (fn [message-tag]
           (let [text (.textContent message-tag)
                 id (. message-tag (getAttribute "id"))
                 author (. message-tag (getAttribute "author"))]
             {:id id :author author :text text}))
         message-tags)))

(defn- get-messages-http [callback]
  (get-request "/messages"
               (fn [e]
                 (let [target (.target e)
                       text (. target (getResponseText))]
                   (callback (unmarshal-messages text))))))

(defn- post-request [uri callback content]
  (XhrIo/send uri (fn [e] (callback)) "post", content))

(defn- marshal-message [message]
  (str "<message author='" (:author message) "'>" (:text message) "</message>"))

(defn- send-message-http [message callback]
  (post-request "/message" (fn [] (callback)) (marshal-message message)))

(defn- stub-client? []
  js/flurfunkStubClient)

(defn get-messages [callback]
  (if (stub-client?)
    (get-messages-stub callback)
    (get-messages-http callback)))

(defn send-message [message callback]
  (if (stub-client?)
    (send-message-stub message callback)
    (send-message-http message callback)))