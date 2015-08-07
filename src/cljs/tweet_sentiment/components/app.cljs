(ns tweet-sentiment.components.app
  (:require [om-tools.dom :as d :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [tweet-sentiment.components.header :as header]
            [tweet-sentiment.components.footer :as footer]
            [tweet-sentiment.components.content :as content]))


(defcomponent app [app owner]
  (render [_]
    (html [:div
           (om/build header/header nil)
           [:div.container.content
            (om/build content/content app)]
           (om/build footer/footer nil)])))