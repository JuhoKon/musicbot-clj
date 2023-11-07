(ns clj-music-cord.helpers.queue
  (:require [clj-music-cord.shared.atoms :as atoms])
  (:use [clojure.data.finger-tree :only [counted-double-list conjl]]))

(defn remove-first-from-queue! []
  (swap! atoms/normal-queue rest))

(defn add-playlist-to-queue [tracks]
  (swap! atoms/normal-queue (fn [value] (reduce conj value tracks))))

(defn add-song-to-queue [track]
  (swap! atoms/normal-queue conj track))

(defn add-playlist-to-queue-front [tracks]
  (swap! atoms/normal-queue (fn [value] (reduce conjl value tracks))))

(defn add-song-to-queue-front [track]
  (swap! atoms/normal-queue conjl track))

(defn reset-queue []
  (reset! atoms/normal-queue (counted-double-list)))