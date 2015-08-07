(ns tweet-sentiment.components.keyword-input
  (:require [om-tools.core :refer-macros [defcomponent]]
            [om-bootstrap.input :as i]
            [tweet-sentiment.utils :as u]
            [om.core :as om]))


(defcomponent keyword-input [state _]
  (render [_]
    (i/input {
              :type        "text"
              :value       (:keyword state)
              :placeholder "Enter Twitter hashtag"
              :on-change   #(u/edit-input state :keyword %)})))

