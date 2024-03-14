(ns frontend.init.controller
  (:require
   [re-frame.core :as re-frame]
   [frontend.db :as db]))

;; Paths

(def base-path [:foo])

;; Events

(defn initialize-db []
  db/default-db)
(re-frame/reg-event-db ::initialize-db initialize-db)

;; Subs
