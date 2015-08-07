(ns tweet-sentiment.components.footer
  (:require [om-tools.dom :as d :include-macros true]
            [sablono.core :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]))

(defcomponent footer [_ _]
  (render [_]
    (html
      [:footer
       [:div.container
        [:p {:class "text-muted"} "This app uses "
         [:a {:href   "https://dandelion.eu/docs/api/datatxt/sent/v1/"
              :target "_blank"} "Dandelion API"]
         " to determine tweet sentiment"]
        [:p {:class "text-muted"}
         [:a {:href "https://github.com/madvas/tweet-sentiment" :target "_blank"} "Source on GitHub"]]]])))
