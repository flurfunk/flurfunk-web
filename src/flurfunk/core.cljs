(ns flurfunk.core
  (:require [flurfunk.client :as client]
            [flurfunk.dom-helpers :as dom]
            [goog.events :as events]
            [goog.string :as string]
            [goog.net.Cookies :as Cookies]
            [goog.ui.Button :as Button]))

(def ^{:private true} last-fetched nil)

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
               [:div#message-list]]]))

(defn- leading-zero [number]
  (str (if (< number 10) "0") number))

(defn- format-timestamp [timestamp]
  (let [date (js/Date. timestamp)]
    (str (. date (getFullYear)) "-"
         (leading-zero (+ (. date (getMonth)) 1)) "-"
         (leading-zero (. date (getDate))) " "
         (leading-zero (. date (getHours))) ":"
         (leading-zero (. date (getMinutes))))))

(defn- map-str [f coll]
  (apply str (map f coll)))

(defn- replace-all
  ([string from to]
     (.replace string (js/RegExp. from "g") to))
  ([string replacements]
     (reduce (fn [string [from to]]
               (replace-all string from to)) string replacements)))

(defn- escape-html [string]
  (replace-all string [["&" "&amp;"]
                       ["\"" "&quot;"]
                       ["<" "&lt;"]
                       [">" "&gt;"]]))

(defn- format-message-text [text]
  (let [escaped-text (escape-html text)
        trimmed-text (string/trim escaped-text)
        text-with-links (replace-all trimmed-text "(https?://\\S*)"
                                     "<a href=\"$1\">$1</a>")
        paragraphs (vec (.split text-with-links "\n\n"))
        text-with-paragraphs (map-str (fn [paragraph]
                                        (str "<p>" paragraph "</p>"))
                                      paragraphs)]
    (dom/html (replace-all text-with-paragraphs "\n" "<br/>"))))

(defn- create-message-element [message]
  (dom/build [:div {:id (str "message-" (:id message))}
              [:span.author (:author message)]
              [:span.timestamp (format-timestamp (:timestamp message))]
              [:div.text (format-message-text (:text message))]]))

(defn- update-message-list [message-list]
  (let [callback (fn [messages]
                   (when (> (count messages) 0)
                     (def last-fetched (:timestamp (first messages)))
                     (doseq [message (reverse messages)]
                       (dom/insert-at message-list
                                      (create-message-element message) 0))))]
    (if (nil? last-fetched)
      (client/get-messages callback)
      (client/get-messages callback last-fetched))))

(defn- send-message [message-list send-button]
  (let [message-textarea (dom/get-element :message-textarea)
        escaped-text (escape-html (.value message-textarea))]
    (.setEnabled send-button false)
    (client/send-message
     {:author (.value (dom/get-element :author-name-input))
      :text escaped-text}
     (fn []
       (set! (.value message-textarea) "")
       (. message-textarea (focus))
       (update-message-list message-list)))))

(defn- update-send-button [send-button]
  (let [author (.value (dom/get-element :author-name-input))
        text (.value (dom/get-element :message-textarea))]
    (.setEnabled send-button (not (or (empty? author)
                                      (empty? text))))))

(defn- set-author-cookie [author]
  (let [cookies (goog.net/Cookies. js/document)
        current-time (. (js/Date.) (getTime))
        expiry-time (+ current-time (* 365 (* 24 (* 60 (* 1000 60)))))]
    (.set cookies "author" author expiry-time)))

(defn- get-author-cookie []
  (let [cookies (goog.net/Cookies. js/document)]
    (.get cookies "author")))

(defn -main []
  (dom/append document.body (create-dom))
  (let [send-button (goog.ui/Button. "Send message")
        message-list (dom/get-element :message-list)]
    (.decorate send-button (dom/get-element :send-button))
    (.setEnabled send-button false)
    (events/listen send-button goog.ui.Component/EventType.ACTION
                   (fn [e] (send-message message-list send-button)))
    (let [author-name-input (dom/get-element :author-name-input)
          message-textarea (dom/get-element :message-textarea)]
      (events/listen author-name-input goog.events/EventType.INPUT
                     (fn [e]
                       (set-author-cookie (.value author-name-input))
                       (update-send-button send-button)))
      (events/listen message-textarea goog.events/EventType.INPUT
                     (fn [e] (update-send-button send-button)))
      (if-let [author (get-author-cookie)]
        (set! (.value author-name-input) author))
      (if (empty? (.value author-name-input))
        (. author-name-input (focus))
        (. message-textarea (focus))))
    (js/setInterval (fn [] (update-message-list message-list)) 1000)
    (update-message-list message-list)))

(-main)