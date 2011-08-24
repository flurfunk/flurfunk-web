(ns flurfunk.core
  (:require [flurfunk.client :as client]
            [goog.dom :as dom]
            [goog.events :as events]
            [goog.ui.Button :as Button]
            [goog.ui.Container :as Container]
            [goog.ui.Control :as Control]))

(defn- create-message-control [message]
  (let [id (:id message)
        author (:author message)
        text (:text message)
        author-span (dom/createDom "span" "author" author)
        text-span (dom/createDom "span" "text" text)
        content (dom/createDom "div" nil author-span text-span)
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
        content-div (dom/createDom "div" "content")
        text-textarea (dom/createDom "textarea")
        send-button (goog.ui/Button. "Send message")
        message-container (goog.ui/Container.)
        sidebar-div (dom/createDom "div" "sidebar")
        author-div (dom/createDom "div")
        author-label (dom/createDom "label" nil "Your name:")
        author-input (dom/createDom "input" {:type "text"})
        update-button (goog.ui/Button. "Update messages")]
    ;; Sidebar
    (dom/appendChild author-div author-label)
    (dom/appendChild author-div author-input)
    (dom/appendChild sidebar-div author-div)
    (.render update-button sidebar-div)
    (events/listen update-button goog.ui.Component/EventType.ACTION
                   (fn [e]
                     (update-message-container message-container)))

    ;; Content
    (dom/appendChild content-div text-textarea)
    (.render send-button content-div)
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
    (.render message-container content-div)

    (dom/appendChild document.body header)
    (dom/appendChild document.body content-div)
    (dom/appendChild document.body sidebar-div)
    (update-message-container message-container)))

(-main)