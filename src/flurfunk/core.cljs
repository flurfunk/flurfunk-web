(ns flurfunk.core
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [goog.ui.Button :as Button]))

(defn greet []
  (window/alert "Hello, World!"))

(defn -main []
  (let [header (dom/createDom "h1" nil "Flurfunk")
        button (goog.ui/Button. "Press me")]
    (dom/appendChild document.body header)
    (.render button document.body)
    (events/listen button goog.ui.Component/EventType.ACTION (fn [e] (greet)))))

(-main)