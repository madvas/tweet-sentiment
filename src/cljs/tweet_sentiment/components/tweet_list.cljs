(ns tweet-sentiment.components.tweet-list
  (:require [om-tools.core :refer-macros [defcomponent]]
            [sablono.core :refer-macros [html]]
            [om-bootstrap.table :refer [table]]
            [om-tools.dom :as d :include-macros true]))

(defcomponent tweet-list [tweets _]
  (render [_]
    (html
      [:table.table
       [:thead
        [:tr
         [:th "User"]
         [:th "Tweet"]
         [:th "Sentinent"]]]
       [:tbody
        (for [tweet tweets]
          [:tr
           [:td [:a {:href (str "https://twitter.com/" (:screen_name tweet)) :target "_blank"} (:name tweet)]]
           [:td (:text tweet)]
           [:td (:sentiment tweet)]])]
       ])))

