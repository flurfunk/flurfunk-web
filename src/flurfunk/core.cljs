(ns flurfunk.core
  (:require [goog.dom :as dom]
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
    (.setId message-control id)
    message-control))

(defn- update-message-container [message-container messages]
  (.removeChildren message-container true)
  (doseq [message messages]
    (.addChild message-container
               (create-message-control message)
               true)))

(defn- fetch-messages []
  [{:id "message-1" :author "author-1" :text "Hello, World 1!"}
   {:id "message-2" :author "author-2" :text "Hello, World 2!"}
   {:id "message-3" :author "author-3" :text "Hello, World 3!"}
   {:id "message-4" :author "author-4" :text "Hello, World 4!"}])

(defn -main []
  (let [header (dom/createDom "h1" nil "Flurfunk")
        message-container (goog.ui/Container.)
        update-button (goog.ui/Button. "Update messages")]
    (dom/appendChild document.body header)
    (.render message-container document.body)
    (.render update-button document.body)
    (events/listen update-button goog.ui.Component/EventType.ACTION
                   (fn [e]
                     (update-message-container message-container
                                               (fetch-messages))))))

(-main)