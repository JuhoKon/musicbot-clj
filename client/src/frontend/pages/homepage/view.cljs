(ns frontend.pages.homepage.view
  (:require
   [frontend.helpers.re-frame :refer [<sub evt>]]
   [frontend.pages.homepage.controller :as homepage-controller]))

(defn main-panel []
  (let [name (<sub [::homepage-controller/header])]
    [:div
     [:h1
      "Hello from " name]]))
