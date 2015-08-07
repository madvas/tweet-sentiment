(ns tweet-sentiment.controllers.api
  (:require [om-sync.util :refer [edn-xhr]]
            [om.core :as om]))

(defn apply-to-state [cursor key response]
  (om/update! cursor key response)
  (om/update! cursor :loading false))


(defmulti api-event
          (fn [message args cursor] message))

(defmethod api-event :keyword-send
  [_ args cursor]
  (om/update! cursor :loading true)
  (edn-xhr
    {:method :post
     :url    "/tweets"
     :data   {:keyword args}
     :on-complete #(apply-to-state cursor :tweets %)}))

(defmethod api-event :default
  [message _]
  (println (str "Api event not found: " message)))
