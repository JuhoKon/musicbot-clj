(ns clj-music-cord.audio-components.scheduler
  (:require [clj-music-cord.commands.channel.commands :as channel-commands])
  (:import (com.sedmelluq.discord.lavaplayer.player.event AudioEventAdapter)
           (com.sedmelluq.discord.lavaplayer.track AudioTrackEndReason)))

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
        ;; Start next track
        )

      ;; Handle different endReason cases
      (condp = (.reason endReason)
        (AudioTrackEndReason/FINISHED)
        ;; A track finished or died by an exception (mayStartNext = true).

        (AudioTrackEndReason/LOAD_FAILED)
        ;; Loading of a track failed (mayStartNext = true).

        (AudioTrackEndReason/STOPPED)
        ;; The player was stopped.

        (AudioTrackEndReason/REPLACED)
        ;; Another track started playing while this had not finished

        (AudioTrackEndReason/CLEANUP)
        ;; Player hasn't been queried for a while; you can put a
        ;; clone of this back to your queue
        ))
    (onTrackException [player track exception]
      ;; An already playing track threw an exception (track end event will still be received separately)
      )
    (onTrackStuck [player track thresholdMs]
      ;; Audio track has been unable to provide us any audio; might want to just start a new track
      )))