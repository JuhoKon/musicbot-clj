(ns frontend.helpers.re-frame
  (:require
   [re-frame.core :as re-frame]))

(defn sub [vec]
  (re-frame/subscribe vec))

(defn <sub [vec]
  @(sub vec))

(defn evt> [vec]
  (re-frame/dispatch vec))
