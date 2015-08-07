(ns tweet-sentiment.async
  (:require [om.core :as om]
            [cljs.core.async :refer [put!]]))

(defn raise! [owner val cursor & args]
  (let [c (om/get-shared owner [:comms :api])]
    (apply put! c (conj val cursor) args)))
