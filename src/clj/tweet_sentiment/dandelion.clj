(ns tweet-sentiment.dandelion
  (:require [clojure.core.async
             :as async
             :refer [put! >! <! <!! go chan alts! timeout]]
            [tweet-sentiment.utils :as u]))

(def dandelion-app-id "b247d5eb")
(def dandelion-app-key "795fd6096094514934fe82fc8760a821")
(def dandelion-url "https://api.dandelion.eu/datatxt/sent/v1")

(def req-opts
  {:query-params
   {:$app_id  dandelion-app-id
    :$app_key dandelion-app-key
    :lang     "en"
    }
   })

(defn get-sentiment [text]
  (let [a (u/http-get dandelion-url (assoc-in req-opts [:query-params :text] text))]
    a))

(defn dandelion-sentiment []
  (let [in (chan 100)
        out (chan 100)]
    (go
      (let [tweets (<! in)
            sentiments (->> tweets
                            (map :text)
                            (map get-sentiment)
                            (async/map vector)
                            )]
        (>! out (map (fn [sen tw]
                       (assoc tw :sentiment (u/round (get-in sen [:sentiment :score]) 2)))
                     (first (alts! [sentiments (timeout 10000)])) tweets)))
      )
    [in out]))
