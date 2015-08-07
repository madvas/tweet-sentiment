(ns tweet_sentiment.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [tweet_sentiment.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'tweet_sentiment.core-test))
    0
    1))
