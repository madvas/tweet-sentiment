(ns tweet-sentiment.utils
  (:require [om.core :as om]))

(defn edit-input
  "Meant to be used in a react event handler, usually for the :on-change event on input.
  Path is the vector of keys you would pass to assoc-in to change the value in state,
  event is the Synthetic React event. Pulls the value out of the event.
  Optionally takes :value as a keyword arg to override the event's value"
  [state name event & {:keys [value]
                       :or {value (.. event -target -value)}}]
  (om/transact! state name #(identity value)))