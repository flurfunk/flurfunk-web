(ns flurfunk.core
  (:require [flurfunk.client :as client]
            [goog.dom :as dom]
            [goog.events :as events]
            [goog.ui.Button :as Button]
            [goog.ui.Container :as Container]
            [goog.ui.Container :as Control]
            [goog.ui.Textarea :as Textarea]))

(defn- create-message-control [message]
  (let [id (:id message)
        author (:author message)
        text (:text message)
        content (dom/createDom "p" nil (str author ": " text))
        message-control (goog.ui/Control. content)]
    (.setId message-control (str "message-" id))
    message-control))

(defn- update-message-container [message-container]
  (client/get-messages
   (fn [messages]
     (.removeChildren message-container true)
     (doseq [message messages]
       (.addChild message-container
                  (create-message-control message)
                  true)))))

(defn -main []
  (let [header (dom/createDom "h1" nil "Flurfunk")
        message-textarea (goog.ui.Textarea.)
        send-button (goog.ui/Button. "Send message")
        message-container (goog.ui/Container.)
        update-button (goog.ui/Button. "Update messages")]
    (dom/appendChild document.body header)
    (.render message-textarea document.body)
    (.render send-button document.body)
    (events/listen send-button goog.ui.Component/EventType.ACTION
                   (fn [e]
                     (let [text (. message-textarea (getValue))]
                       (client/send-message
                        ;; TODO: Let the user enter a name
                        {:author "anonymous" :text text}
                        (fn []
                          (.setValue message-textarea "")
                          (update-message-container message-container))))))
    (.render message-container document.body)
    (.render update-button document.body)
    (events/listen update-button goog.ui.Component/EventType.ACTION
                   (fn [e]
                     (update-message-container message-container)))
    (update-message-container message-container)))

(-main)