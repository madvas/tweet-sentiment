(ns tweet-sentiment.routes
  (:require
    [secretary.core :as sec :include-macros true]
    [goog.events :as events]
    [goog.history.EventType :as EventType]
    [om.core :as om]
    [tweet-sentiment.components.app :as app])
  (:import goog.History))

(defonce history (History.))

(defn setup-history! []
  (sec/set-config! :prefix "#")
  (events/listen history EventType/NAVIGATE #(-> % .-token sec/dispatch!))
  (.setEnabled history true))

(defn set-page! [page state opts]
  (swap! state assoc :page page)
  (om/root app/app state opts))


(defn define-routes! [state opts]
  (setup-history!)
  (sec/defroute home-page "*" []
                (set-page! :home state opts))

  (sec/dispatch! "/"))