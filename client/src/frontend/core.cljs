(ns frontend.core
  (:require
   [frontend.config :as config]
   [frontend.pages.homepage.view :as view]
   [frontend.init.controller :as init-controller]
   [re-frame.core :as re-frame]
   [reagent.dom :as rdom]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [view/main-panel] root-el)))

(defn init []
  (re-frame/dispatch-sync [::init-controller/initialize-db])
  (dev-setup)
  (mount-root))
