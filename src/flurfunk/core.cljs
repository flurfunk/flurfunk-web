(ns flurfunk.core
  (:require [flurfunk.client :as client]
            [goog.dom :as dom]
            [goog.events :as events]
            [goog.ui.Button :as Button]
            [goog.ui.Container :as Container]
            [goog.ui.Container :as Control]))

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
        author-div (dom/createDom "div")
        author-label (dom/createDom "label" nil "Your name:")
        author-input (dom/createDom "input" {:type "text"})
        text-div (dom/createDom "div")
        text-label (dom/createDom "label" nil "Your message:")
        text-textarea (dom/createDom "textarea")
        send-button (goog.ui/Button. "Send message")
        message-container (goog.ui/Container.)
        update-button (goog.ui/Button. "Update messages")]
    (dom/appendChild document.body header)
    
    (dom/appendChild author-div author-label)
    (dom/appendChild author-div author-input)
    (dom/appendChild document.body author-div)
    
    (dom/appendChild text-div text-label)
    (dom/appendChild text-div text-textarea)
    (dom/appendChild document.body text-div)
    
    (.render send-button document.body)
    (events/listen send-button goog.ui.Component/EventType.ACTION
                   (fn [e]
                     (let [author (.value author-input)
                           text (.value text-textarea)]
                       (when (not (or (empty? author) (empty? text)))
                         (client/send-message
                          {:author author :text text}
                          (fn []
                            (set! (.value text-textarea) "")
                            (update-message-container message-container)))))))

    (.render update-button document.body)
    (events/listen update-button goog.ui.Component/EventType.ACTION
                   (fn [e]
                     (update-message-container message-container)))
    (.render message-container document.body)

    (update-message-container message-container)))

(-main)