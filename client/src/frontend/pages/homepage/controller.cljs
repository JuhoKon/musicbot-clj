(ns frontend.pages.homepage.controller
  (:require
   [re-frame.core :as re-frame]
   [frontend.pages.controller :as pages-controller]))

;; Paths

(def base-path (conj pages-controller/base-path :homepage))
(def header-path (conj base-path :header))

;; Events

;; Subs

(defn get-header [db]
  (get-in db header-path))

(re-frame/reg-sub ::header get-header)