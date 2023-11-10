(ns clj-music-cord.audio-components.scheduler
  (:import (com.sedmelluq.discord.lavaplayer.player.event AudioEventAdapter))
  (:require [clj-music-cord.shared.atoms :as atoms]
            [clj-music-cord.helpers.queue :as queue]))

(def track-scheduler
  (proxy [AudioEventAdapter] []
    (onPlayerPause [player])

    (onPlayerResume [player])

    (onTrackStart [player track])

    (onTrackEnd [player track endReason]
      (when (.mayStartNext endReason)
        (if (empty? @atoms/queue-atom)
          (when @atoms/repeat-mode-atom
            (.startTrack player (.makeClone track) true))
          (let [song (first @atoms/queue-atom)]
            (.startTrack player (.makeClone song) true)
            (if @atoms/repeat-mode-atom
              (do
                (queue/remove-first-from-queue!)
                (queue/add-song-to-queue track))
              (queue/remove-first-from-queue!))))))

    (onTrackException [player track exception])

    (onTrackStuck [player track thresholdMs]
      (when-not (empty? @atoms/queue-atom)
        (let [song (first @atoms/queue-atom)]
          (.startTrack player (.makeClone song) true)
          (queue/remove-first-from-queue!))))))