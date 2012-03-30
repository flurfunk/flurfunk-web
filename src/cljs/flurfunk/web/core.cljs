(ns flurfunk.web.core
  (:require [flurfunk.web.client :as client]
            [flurfunk.web.dom-helpers :as dom]
            [goog.dom.classes :as classes]
            [goog.events :as events]
            [goog.fx.dom :as fx-dom]
            [goog.string :as string]
            [goog.style :as style]
            [goog.net.Cookies :as Cookies]
            [goog.ui.Button :as Button]))

(def ^{:private true} title "Flurfunk")
(def ^{:private true} last-fetched nil)
(def ^{:private true} active true)
(def ^{:private true} unread-messages 0)

(defn- create-dom []
  (dom/build [:div#content
              [:h1 title]
              [:div#messages
               [:div#message-input
                [:div
                 [:label "Your name:"]
                 [:input#author-name-input {:type "text"}]]
                [:textarea#message-textarea]
                [:button#send-button "Send message"]
                [:div#waiting-indication]]
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
                                     "<a href=\"$1\" target=\"_blank\">$1</a>")
        paragraphs (vec (.split text-with-links "\n\n"))
        text-with-paragraphs (map-str #(str "<p>" % "</p>") paragraphs)]
    (dom/html (replace-all text-with-paragraphs "\n" "<br/>"))))

(defn- create-message-element
  ([message]
     (create-message-element message false))
  ([message first-unread]
     (dom/build [:div {:id (str "message-" (:id message))
                       :class (if first-unread "first-unread")}
                 [:span.author (:author message)]
                 [:span.timestamp (format-timestamp (:timestamp message))]
                 [:div.text (format-message-text (:text message))]])))

(defn- append-message
  ([message-list message flags]
     (let [message-element (create-message-element
                            message (contains? flags :first-unread))]
       (dom/insert-at message-list message-element 0)
       (if (contains? flags :animate)
         (. (fx-dom/ResizeHeight. message-element 0
                                  (.offsetHeight message-element) 500)
            (play))))))

(defn- append-messages [message-list messages]
  (let [reversed-messages (reverse messages)
        first-unread (and (not active) (= unread-messages 0))
        flags (conj #{} (if (= (count messages) 1) :animate))]
    (when first-unread
      (doseq [unread-message-div
              (dom/query-elements "div#message-list>*.first-unread")]
        (classes/remove unread-message-div "first-unread")))
    (append-message message-list (first reversed-messages)
                    (conj flags (if first-unread :first-unread)))
    (doseq [message (rest reversed-messages)]
      (append-message message-list message flags))))

(defn- update-title []
  (set! (.-title js/document) (str (if (> unread-messages 0)
                                    (str "(" unread-messages ") "))
                                  title)))

(defn- fade-waiting-indication [fade-in]
  (let [waiting-indication (dom/get-element :waiting-indication)
        is-shown (style/isElementShown waiting-indication)]
    (if (or (and fade-in (not is-shown))
            (and (not fade-in) is-shown))
      (. (new (if fade-in fx-dom/FadeInAndShow fx-dom/FadeOutAndHide)
              waiting-indication 500)
         (play)))))

(defn- show-waiting-indication
  ([] (show-waiting-indication true))
  ([show] (let [waiting-indication (dom/get-element :waiting-indication)
                is-shown (style/isElementShown waiting-indication)]
            (if (or (and show (not is-shown))
                    (and (not show) is-shown))
              (. (new (if show fx-dom/FadeInAndShow fx-dom/FadeOutAndHide)
                      waiting-indication 500)
                 (play))))))

(defn- hide-waiting-indication []
  (show-waiting-indication false))

(defn- update-message-list [message-list]
  (let [waiting (atom true)
        callback (fn [messages]
                   (let [message-count (count messages)]
                     (if (> message-count 0)
                       (let [latest-timestamp (:timestamp (first messages))]
                         (when (or (nil? last-fetched)
                                   (> latest-timestamp last-fetched))
                           (def last-fetched latest-timestamp)
                           (append-messages message-list messages)
                           (when (not active)
                             (def unread-messages (+ unread-messages
                                                     message-count))
                             (update-title)))))
                     (hide-waiting-indication)
                     (compare-and-set! waiting true false)))]
    (js/setTimeout (fn [] (if @waiting (show-waiting-indication))) 500)
    (if (nil? last-fetched)
      (client/get-messages callback)
      (client/get-messages callback last-fetched))))

(defn- animate-element-height [element new-height]
  (. (fx-dom/ResizeHeight. element (.offsetHeight element) new-height 100)
     (play)))

(defn- begin-composing [message-textarea]
  (animate-element-height message-textarea 90))

(defn- end-composing [message-textarea]
  (animate-element-height message-textarea 30))

(defn- send-message [message-list send-button]
  (let [message-textarea (dom/get-element :message-textarea)
        escaped-text (escape-html (.value message-textarea))]
    (.setEnabled send-button false)
    (client/send-message
     {:author (.value (dom/get-element :author-name-input))
      :text escaped-text}
     (fn []
       (set! (.-value message-textarea) "")
       (end-composing message-textarea)
       (update-message-list message-list)))))

(defn- update-send-button [send-button]
  (let [author (string/trim (.value (dom/get-element :author-name-input)))
        text (string/trim (.value (dom/get-element :message-textarea)))]
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

(defn- prepare-elements [author-name-input message-textarea send-button
                         message-list]
  (let [send-button-widget (goog.ui/Button. "Send message")]
    (.decorate send-button-widget send-button)
    (.setEnabled send-button-widget false)
    (events/listen send-button-widget goog.ui.Component/EventType.ACTION
                   #(send-message message-list send-button-widget))
    (events/listen author-name-input goog.events/EventType.INPUT
                   (fn [e]
                     (set-author-cookie (.value author-name-input))
                     (update-send-button send-button-widget)))
    (events/listen message-textarea goog.events/EventType.INPUT
                   #(update-send-button send-button-widget))
    (events/listen message-textarea goog.events/EventType.FOCUS
                   #(begin-composing message-textarea))
    (events/listen message-textarea goog.events/EventType.BLUR
                   (fn [e]
                     (if (empty? (.value message-textarea))
                       (end-composing message-textarea))))))

(defn -main []
  (dom/append document.body (create-dom))
  (let [author-name-input (dom/get-element :author-name-input)
        message-textarea (dom/get-element :message-textarea)
        send-button (dom/get-element :send-button)
        message-list (dom/get-element :message-list)]
    (prepare-elements author-name-input message-textarea send-button
                      message-list)
    (if-let [author (get-author-cookie)]
      (set! (.-value author-name-input) author))
    (if (empty? (.value author-name-input))
      (. author-name-input (focus)))
    (js/setInterval #(update-message-list message-list) 1000)
    (update-message-list message-list)
    (set! (.-onfocus js/window) (fn []
                                 (def active true)
                                 (def unread-messages 0)
                                 (update-title)))
    (set! (.-onblur js/window) (fn []
                                (def active false)
                                (if (empty? (.value message-textarea))
                                  (. message-textarea (blur)))))))

(-main)