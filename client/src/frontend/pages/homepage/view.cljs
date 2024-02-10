(ns frontend.pages.homepage.view
  (:require
   [re-frame.core :as re-frame]
   [frontend.pages.homepage.controller :as homepage-controller]))

(defn main-panel []
  (let [name (re-frame/subscribe [::homepage-controller/header])]
    [:div
     [:h1
      "Hello from " @name]]))
