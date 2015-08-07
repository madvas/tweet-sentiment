(ns tweet-sentiment.server
  (:require [clojure.java.io :as io]
            [tweet-sentiment.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel start-less]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [resources]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [net.cgrand.reload :refer [auto-reload]]
            [ring.middleware.reload :as reload]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [tweet-sentiment.utils :refer [generate-response]]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [tweet-sentiment.tweets :refer [tweets]]
            [tweet-sentiment.dandelion :refer [dandelion-sentiment]]
            [clojure.core.async :refer [>!! <!! put! take! pipe chan]]
            [cemerick.drawbridge :as drawbridge]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
            )
  (:gen-class))

(defonce server (atom nil))

(deftemplate page (io/resource "index.html") []
             [:body] (if is-dev? inject-devmode-html identity))

(defn get-tweets-sentiment [keyword]
  (let [[tw-in tw-out] (tweets)
        [ds-in ds-out] (dandelion-sentiment)]
    (pipe tw-out ds-in)
    (put! tw-in keyword)
    (let [tweets (<!! ds-out)]
      (generate-response (reverse (sort-by :sentiment tweets)) {:status :ok}))))

(defroutes routes
           (resources "/")
           (resources "/react" {:root "react"})
           (POST "/tweets" [keyword] (get-tweets-sentiment keyword))
           (GET "/*" req (page)))

(defn authenticated? [name pass]
  (= [name pass] [(System/getenv "AUTH_USER") (System/getenv "AUTH_PASS")]))


;(defn wrap-drawbridge [handler]
;  (fn [req]
;    (let [handler (if (= "/repl" (:uri req))
;                    (-> handler
;                        (wrap-basic-authentication authenticated?)
;                        (drawbridge/ring-handler))
;                    handler)]
;      (handler req))))

(defn wrap-drawbridge [handler]
  (fn [req]
    (if (= "/repl" (:uri req))
      (drawbridge/ring-handler req)
      (handler req))))


(def http-handler
  (if is-dev?
    (-> (wrap-defaults #'routes api-defaults)
        reload/wrap-reload
        wrap-edn-params)
    (wrap-defaults routes api-defaults)))

(defn run-web-server [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (println (format "Starting web server on port %d." port))
    (reset! server (run-server http-handler {:port port :join? false}))))

(defn run-auto-reload [& [port]]
  (auto-reload *ns*)
  (start-figwheel)
  (start-less))

(defn run [& [port]]
  (when is-dev?
    (run-auto-reload))
  (run-web-server port))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 0)
    (reset! server nil)))

(defn restart-server []
  (stop-server)
  (run-web-server))

(defn -main [& [port]]
  (run port))
