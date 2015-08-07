(ns tweet-sentiment.tweets
  (:require [clojure.core.async
             :as a
             :refer [>! <! go chan]]
            [twitter.oauth :as o]
            [twitter.api.restful :as r]
            [tweet-sentiment.utils :as u]))

(def my-creds (o/make-oauth-creds "9Gqgi4ZNREv46qsqHxOf3shWX"
                                  "9e1g84B0JLQL8Ptq6qNLzP0207Aijyh9lBmA4xcvycZ0K73g7N"
                                  "2159227117-zd1TjAGoh1dd6XiXRhBAI8KZwQoiNnKIvSISwlc"
                                  "tN6ZO73LNLdZMx6M5N0kKZSd0sHImRO9FkPihqr5MILQT"))

(def limit 12)

(defn get-tweets [keyword]
  (let [res (r/search-tweets :oauth-creds my-creds
                             :params {:q           (str "#" keyword)
                                      :result-type "popular"
                                      :lang        "en"})
        statuses (get-in res [:body :statuses])]
    (->> statuses
         (map #(u/select-in % [:text [:user :screen_name] [:user :name]]))
         (take limit))))

(defn tweets []
  (let [in (chan)
        out (chan)]
    (go []
        (let [keyword (<! in)]
          (>! out (get-tweets keyword))))
    [in out]))