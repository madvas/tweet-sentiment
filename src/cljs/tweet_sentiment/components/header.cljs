(ns tweet-sentiment.components.header
  (:require [om-tools.dom :as d :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [om-bootstrap.nav :as n]))

(defcomponent header [app owner]
  (render [_]
    (n/navbar
      {:brand (d/a {:href "#"} "Tweet Sentiment")}
      )))
