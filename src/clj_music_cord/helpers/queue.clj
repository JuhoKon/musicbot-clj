(ns clj-music-cord.helpers.queue
  (:require [clj-music-cord.shared.atoms :as atoms])
  (:use [clojure.data.finger-tree :only [counted-double-list conjl]]))

(defn remove-first-from-queue! []
  (swap! atoms/queue-atom rest))

(defn add-playlist-to-queue [tracks]
  (swap! atoms/queue-atom (fn [value] (reduce conj value tracks))))

(defn add-song-to-queue [track]
  (swap! atoms/queue-atom conj track))

(defn add-playlist-to-queue-front [tracks]
  (swap! atoms/queue-atom (fn [value] (reduce conjl value tracks))))

(defn add-song-to-queue-front [track]
  (swap! atoms/queue-atom conjl track))

(defn reset-queue []
  (reset! atoms/queue-atom (counted-double-list)))

(defn shuffle-queue []
  (swap! atoms/queue-atom #(reduce conj (counted-double-list) (shuffle (vec %)))))