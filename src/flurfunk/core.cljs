(ns flurfunk.core
  (:require [flurfunk.client :as client]
            [flurfunk.dom-helpers :as dom]
            [goog.events :as events]
            [goog.ui.Button :as Button]
            [goog.ui.Container :as Container]
            [goog.ui.Control :as Control]))

(defn- create-dom []
  (dom/build [:div#content
              [:h1 "Flurfunk"]
              [:div#messages
               [:div#message-input
                [:div
                 [:label "Your name:"]
                 [:input#author-name-input {:type "text"}]]
                [:textarea#message-textarea]
                [:button#send-button "Send message"]]
               [:div#message-container]]]))

(defn- create-message-control [message]
  (let [content (dom/build [:div
                            [:span.author (:author message)]
                            [:span.text (:text message)]])
        message-control (goog.ui/Control. content)]
    (.setId message-control (str "message-" (:id message)))
    message-control))

(defn- update-message-container [message-container]
  (client/get-messages
   (fn [messages]
     (.removeChildren message-container true)
     (doseq [message messages]
       (.addChild message-container (create-message-control message) true)))))

(defn- send-message [message-container]
  (let [message-textarea (dom/get-element :message-textarea)
        text (.value message-textarea)
        author (.value (dom/get-element :author-name-input))]
    (when (not (or (empty? author) (empty? text)))
      (client/send-message
       {:author author :text text}
       (fn []
         (set! (.value message-textarea) "")
         (update-message-container message-container))))))

(defn -main []
  (dom/append document.body (create-dom))
  (let [send-button (goog.ui/Button. "Send message")
        message-container (goog.ui/Container.)]
    (.decorate send-button (dom/get-element :send-button))
    (events/listen send-button goog.ui.Component/EventType.ACTION
                   (fn [e] (send-message message-container)))
    (.decorate message-container (dom/get-element :message-container))
    (js/setInterval (fn [] (update-message-container message-container)) 1000)
    (update-message-container message-container)))

(-main)