(ns frontend.common.controller
  (:require [frontend.init.controller :as init-controller]))

;; Paths

(def base-path (conj init-controller/base-path :common))

;; Events

;; Subs
