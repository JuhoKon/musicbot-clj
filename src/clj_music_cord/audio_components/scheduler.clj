(ns clj-music-cord.audio-components.scheduler
  (:import (com.sedmelluq.discord.lavaplayer.player.event AudioEventAdapter)
           (com.sedmelluq.discord.lavaplayer.track AudioTrackEndReason))
  (:require [clj-music-cord.commands.channel.commands :as channel-commands]
            [clj-music-cord.shared.atoms :as atoms]
            [clj-music-cord.helpers.queue :as queue]))

(def track-scheduler
  (proxy [AudioEventAdapter] []
    (onPlayerPause [player]
      ;; Player was paused
      )
    (onPlayerResume [player]
      ;; Player was resumed
      )
    (onTrackStart [player track]
      ;; A track started playing
      )
    (onTrackEnd [player track endReason]
      (when (.mayStartNext endReason)
        (when-not (empty? @atoms/normal-queue)
          (let [song (first @atoms/normal-queue)
                info (.getInfo song)]
            (.startTrack player (.makeClone song) true)
            (queue/remove-first-from-queue!)))))
    (onTrackException [player track exception]
      ;; An already playing track threw an exception (track end event will still be received separately)
      )
    (onTrackStuck [player track thresholdMs]
      ;; Audio track has been unable to provide us any audio; might want to just start a new track
      )))