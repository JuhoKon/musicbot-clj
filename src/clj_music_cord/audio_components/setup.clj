(ns clj-music-cord.audio-components.setup
  (:require [clj-music-cord.audio-components.audio-provider :as audio-provider]
            [clj-music-cord.audio-components.scheduler :as scheduler])
  (:import (com.sedmelluq.discord.lavaplayer.player DefaultAudioPlayerManager)
           (com.sedmelluq.discord.lavaplayer.source AudioSourceManagers)))

(defn setup-audio-components [guild-id]
  (let [player-manager (DefaultAudioPlayerManager.)
        player (.createPlayer player-manager)
        scheduler (scheduler/track-scheduler guild-id)
        provider (audio-provider/create-lava-player-audio-provider player)
        audio-components {:player-manager player-manager
                          :player player
                          :scheduler scheduler
                          :provider provider}]
    (.addListener player scheduler)
    (.. AudioSourceManagers (registerRemoteSources player-manager))
    audio-components))