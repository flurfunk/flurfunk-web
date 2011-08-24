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

(defn- send-message [message-container send-button]
  (let [message-textarea (dom/get-element :message-textarea)]
    (client/send-message
     {:author (.value (dom/get-element :author-name-input))
      :text (.value message-textarea)}
     (fn []
       (set! (.value message-textarea) "")
       (.setEnabled send-button false)
       (update-message-container message-container)))))

(defn -main []
  (dom/append document.body (create-dom))
  (let [send-button (goog.ui/Button. "Send message")
        message-container (goog.ui/Container.)]
    (.decorate send-button (dom/get-element :send-button))
    (events/listen send-button goog.ui.Component/EventType.ACTION
                   (fn [e] (send-message message-container send-button)))
    (.decorate message-container (dom/get-element :message-container))
    (let [author-name-input (dom/get-element :author-name-input)
          message-textarea (dom/get-element :message-textarea)
          change-handler
          (fn [e]
            (.setEnabled send-button
                         (not (or (empty? (.value author-name-input))
                                  (empty? (.value message-textarea))))))]
      (doseq [element [author-name-input message-textarea]]
        (events/listen element goog.events/EventType.KEYUP change-handler)
        (events/listen element goog.events/EventType.CHANGE change-handler)))
    (.setEnabled send-button false)
    (js/setInterval (fn [] (update-message-container message-container)) 1000)
    (update-message-container message-container)))

(-main)