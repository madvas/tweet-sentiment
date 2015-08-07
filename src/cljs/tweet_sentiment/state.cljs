(ns tweet-sentiment.state
  (:require [om.core :as om]))

(defn initial-state []
  {:hello "there"})

(defn tweets []
  (om/ref-cursor (:tweets (om/root-cursor initial-state))))