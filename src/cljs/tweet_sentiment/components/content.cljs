(ns tweet-sentiment.components.content
  (:require [om-tools.core :refer-macros [defcomponentmethod]]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [om-bootstrap.button :as b]
            [om-bootstrap.random :as r]
            [tweet-sentiment.components.home :as home]
            ))


(defmulti content
          (fn [state owner] (:page state)))

(defcomponentmethod content :default
  [state owner]
  (render [_]
    (om/build home/home state)))