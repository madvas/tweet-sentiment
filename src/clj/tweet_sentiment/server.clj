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
            [ring.middleware.params :as params]
            [ring.middleware.keyword-params :as keyword-params]
            [ring.middleware.nested-params :as nested-params]
            [ring.middleware.session :as session]
            [compojure.handler :refer [site]]
            )
  (:gen-class))

(defn get-tweets-sentiment [keyword]
  (let [[tw-in tw-out] (tweets)
        [ds-in ds-out] (dandelion-sentiment)]
    (pipe tw-out ds-in)
    (put! tw-in keyword)
    (let [tweets (<!! ds-out)]
      (generate-response (reverse (sort-by :sentiment tweets)) {:status :ok}))))

(defonce server (atom nil))

(deftemplate page (io/resource "index.html") []
             [:body] (if is-dev? inject-devmode-html identity))

(defroutes routes
           (resources "/")
           (resources "/react" {:root "react"})
           (POST "/tweets" [keyword] (get-tweets-sentiment keyword))
           (GET "/*" req (page)))

(defn authenticated? [name pass]
  (= [name pass] [(System/getenv "AUTH_USER") (System/getenv "AUTH_PASS")]))

(def drawbridge-handler
  (-> (drawbridge/ring-handler)
      (keyword-params/wrap-keyword-params)
      (nested-params/wrap-nested-params)
      (params/wrap-params)
      (session/wrap-session)))

(defn http-handler [handler]
  (-> handler
      (wrap-defaults api-defaults)
      wrap-edn-params))

(defn wrap-http [handler]
  (fn [req]
    (let [handler (if (= "/repl" (:uri req))
                    (wrap-basic-authentication drawbridge-handler authenticated?)
                    (if is-dev?
                      (-> handler
                          http-handler
                          reload/wrap-reload)
                      (-> handler
                          http-handler)))]
      (handler req))))

(defn run-web-server [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (println (format "Starting web server on port %d." port))
    (reset! server
            (run-server (wrap-http #'routes) {:port port :join? false})
            )))

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
    (@server :timeout 0)
    (reset! server nil)))

(defn restart-server []
  (stop-server)
  (run-web-server))

(defn -main [& [port]]
  (run port))
