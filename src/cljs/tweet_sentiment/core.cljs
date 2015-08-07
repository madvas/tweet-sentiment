(ns tweet-sentiment.core
  (:require [tweet-sentiment.routes :as r]
            [tweet-sentiment.state :refer [initial-state]]
            [cljs.core.async :as a :refer [chan]]
            [tweet-sentiment.controllers.api :as api]
            [om.core :as om]
            [om.dom :as dom :include-macros true])
  (:require-macros [cljs.core.async.macros :as am :refer [go go-loop alt!]]))

(enable-console-print!)

(def target-element (. js/document (getElementById "app")))

(def api-ch
  (chan))

(def res-ch
  (chan))


(defn get-state []
  (atom (assoc (initial-state) :comms {:api      api-ch
                                       :res      res-ch
                                       :api-mult (a/mult api-ch)
                                       :res-mult (a/mult res-ch)})))

(defn log-state-changes [state]
  (add-watch state :watcher
             (fn [key atom old-state new-state]
               (.log js/console (clj->js new-state)))))

(defn api-handler [value]
  (let [message (first value)
        args (second value)
        cursor (last value)]
    (api/api-event message args cursor)))

(defn main []
  (let [state (get-state)
        res-tap (chan)
        api-tap (chan)
        comms (:comms @state)]
    ;(log-state-changes state)

    (a/tap (:api-mult comms) api-tap)
    (a/tap (:res-mult comms) res-tap)

    (go (while true
          (alt!
            api-tap ([v] (api-handler v)))))

    (r/define-routes!
      state
      {:shared {:comms comms}
       :target target-element
       })))
