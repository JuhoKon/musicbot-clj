(ns clj-music-cord.audio-components.scheduler
  (:import (com.sedmelluq.discord.lavaplayer.player.event AudioEventAdapter))
  (:require [clj-music-cord.state.guild.repeat-mode :as repeat-mode]
            [clj-music-cord.state.guild.queue :as queue]))

(defn track-scheduler [guild-id]
  (proxy [AudioEventAdapter] []
    (onPlayerPause [player])

    (onPlayerResume [player])

    (onTrackStart [player track])

    (onTrackEnd [player track endReason]
      (when (.mayStartNext endReason)
        (if (empty? (queue/get-queue guild-id))
          (when (repeat-mode/get-repeat-mode guild-id)
            (.startTrack player (.makeClone track) true))
          (let [song (first (queue/get-queue guild-id))]
            (.startTrack player (.makeClone song) true)
            (if (repeat-mode/get-repeat-mode guild-id)
              (do
                (queue/remove-first-from-queue! guild-id)
                (queue/add-song-to-queue guild-id track))
              (queue/remove-first-from-queue! guild-id))))))

    (onTrackException [player track exception])

    (onTrackStuck [player track thresholdMs]
      (when-not (empty? (queue/get-queue guild-id))
        (let [song (first (queue/get-queue guild-id))]
          (.startTrack player (.makeClone song) true)
          (queue/remove-first-from-queue! guild-id))))))