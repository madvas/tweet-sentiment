(ns tweet-sentiment.components.home
  (:require [om-tools.core :refer-macros [defcomponent]]
            [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [om-bootstrap.button :as b]
            [cljs.core.async :refer [put!]]
            [tweet-sentiment.components.keyword-input :refer [keyword-input]]
            [tweet-sentiment.components.tweet-list :refer [tweet-list]]
            [tweet-sentiment.async :refer [raise!]]))

(defcomponent home [state owner]
  (render [_]
    (html
      [:div
       [:div.col-xs-12
        [:div.col-xs-9
         (om/build keyword-input state)]
        (b/button {:class     "col-xs-3"
                   :bs-style  "primary"
                   :disabled? (or (empty? (:keyword state)) (:loading state))
                   :on-click  #(raise! owner [:keyword-send (:keyword state)] state)
                   } (if (:loading state) "Loading..." "Send"))]
       [:div.col-xs-12
        (om/build tweet-list (:tweets state))]])))

