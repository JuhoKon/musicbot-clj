(ns clj-music-cord.state.guild.queue
  (:require [clj-music-cord.state.guild.state :as guild-state]
            [clojure.data.finger-tree :as finger-tree]))

(defn update-queue! [guild-id queue]
  (swap! guild-state/state-by-guild-id assoc-in [guild-id :queue] queue))

(defn get-queue [guild-id]
  (get-in @guild-state/state-by-guild-id [guild-id :queue]))

(defn remove-first-from-queue! [guild-id]
  (update-queue! guild-id (rest (get-queue guild-id))))

(defn add-playlist-to-queue [guild-id tracks]
  (update-queue! guild-id (reduce conj (get-queue guild-id) tracks)))

(defn add-song-to-queue [guild-id track]
  (update-queue! guild-id (conj (get-queue guild-id) track)))

(defn add-playlist-to-queue-front [guild-id tracks]
  (update-queue! guild-id (reduce finger-tree/conjl (get-queue guild-id) tracks)))

(defn add-song-to-queue-front [guild-id track]
  (update-queue! guild-id (finger-tree/conjl (get-queue guild-id) track)))

(defn reset-queue [guild-id]
  (update-queue! guild-id (finger-tree/counted-double-list)))

(defn shuffle-queue [guild-id]
  (update-queue! guild-id (reduce conj (finger-tree/counted-double-list) (shuffle (vec (get-queue guild-id))))))