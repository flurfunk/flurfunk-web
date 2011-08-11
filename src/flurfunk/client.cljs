(ns flurfunk.client)

;; TODO: Get the messages from the server.

(def messages [])

(defn get-messages [callback]
  (callback messages))

(defn send-message [message callback]
  (def messages (conj messages (conj message {:id (. (js/Date.) (getTime))})))
  (callback))

