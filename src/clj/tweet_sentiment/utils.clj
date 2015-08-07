(ns tweet-sentiment.utils
  (:require [clojure.core.async :refer [chan put! go <! <!!]]
            [org.httpkit.client :as http]
            [cheshire.core :as cheshire]))

(defn generate-response [data & [status]]
  {:status  (or status 200)
   :headers {"Content-Type" "application/edn"}
   :body    (pr-str data)})

(defn partial-right
  "Takes a function f and fewer than the normal arguments to f, and
 returns a fn that takes a variable number of additional args. When
 called, the returned function calls f with additional args + args."
  ([f] f)
  ([f arg1]
   (fn [& args] (apply f (concat args [arg1]))))
  ([f arg1 arg2]
   (fn [& args] (apply f (concat args [arg1 arg2]))))
  ([f arg1 arg2 arg3]
   (fn [& args] (apply f (concat args [arg1 arg2 arg3]))))
  ([f arg1 arg2 arg3 & more]
   (fn [& args] (apply f (concat args (concat [arg1 arg2 arg3] more))))))


(defn select-in [m keys]
  "Returns a map containing only those entries in map whose key is in keys
   Vector key is interpreted as nested structure

   (select-in {:a 1 :b {:c 1}} [:a [:b :c]])
   => {:c 1, :a 1}"
  (loop [acc {} [k & ks] (seq keys)]
    (if k
      (recur
        (if (sequential? k)
          (assoc acc (last k) (get-in m k))
          (assoc acc k (get m k)))
        ks)
      acc)))


(defn http-get-chan [url options]
  (let [c (chan)]
    (http/get url options
              (fn [r] (put! c r)))
    c))

(defn http-get [url options]
  (go
    (-> (http-get-chan url options)
        <!
        :body
        (cheshire/parse-string true))))

(defn round
  "Round down a double to the given precision (number of significant digits)"
  [d precision]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/floor (* d factor)) factor)))


