(ns com.eelchat.client
  (:require
    [shadow.graft :as graft]
    [shadow.cljs.modern :refer (js-await)]
    [cljs.reader :as reader]))

;; grab csrf token from html head meta, could alternatively be set via a graft
(def csrf-token (.-content (js/document.querySelector "meta[name=x-csrf-token]")))

;; look ma, no libs. these should likely be library functions
;; should obviously do more validation and error checking here, but for our purposes this is enough
(defn req [href opts]
  (js-await [res (js/fetch href (clj->js (assoc-in opts [:headers "x-csrf-token"] csrf-token)))]
    (.text res)))

(defn append-html [container html]
  (let [temp (js/document.createElement "template")]
    (set! temp -innerHTML html)
    (.appendChild container (.-content temp))
    ))


;; our graft scions
(defmethod graft/scion "channel-ui" [{:keys [href] :as opts} _]
  (let [messages (js/document.getElementById "messages")
        form (js/document.getElementById "chat-form")
        textarea (.querySelector form "[data-ref=text]")

        scroll!
        (fn []
          ;; FIXME: only scroll if not scrolled up? htmx variant didn't do that so we don't
          (.scrollTo messages #js {:top (.-scrollHeight messages) :behavior "smooth"}))]

    (scroll!)

    (.addEventListener form "submit"
      (fn [e]
        (.preventDefault e)
        (js-await [body (req href {:method "POST" :body (js/FormData. form)})]
          (append-html messages body)
          (scroll!)
          (set! textarea -value "")
          )))

    ;; absolute most basic websocket setup possible
    ;; you'd likely want some kind of automatic reconnect and stuff
    (let [ws (js/WebSocket. (str "ws://" js/document.location.host href "/connect"))]
      (.addEventListener ws "message"
        (fn [e]
          ;; FIXME: I didn't figure out why the server never actually sends anything, so this code never actually runs
          ;; but it should work in theory
          (let [new-html (.-data e)]
            (append-html messages new-html)
            (scroll!)
            ))))))

(defmethod graft/scion "channel-delete"
  [{:keys [href active title community] :as opts} button]
  (.addEventListener button "click"
    (fn [e]
      (.preventDefault e)
      (when (js/confirm (str "Delete " title "?"))
        (js-await [body (req href {:method "DELETE"})]
          (if active
            (set! js/window.location -href (str "/community/" community))
            (.remove (.-parentElement button))
            ))))))

(defn init []
  (graft/init reader/read-string js/document.body))
