(ns clj-music-cord.audio-components.setup
  (:require [clj-music-cord.audio-components.audio-provider :as audio-provider]
            [clj-music-cord.audio-components.result-handlers.play :as load-handler-play]
            [clj-music-cord.audio-components.result-handlers.playnext :as load-handler-playnext]
            [clj-music-cord.audio-components.scheduler :as scheduler])
  (:import (com.sedmelluq.discord.lavaplayer.player DefaultAudioPlayerManager)
           (com.sedmelluq.discord.lavaplayer.source AudioSourceManagers)))

(defn setup-audio-components []
  (let [player-manager (DefaultAudioPlayerManager.)
        player (.createPlayer player-manager)
        scheduler scheduler/track-scheduler
        _ (.addListener player scheduler)
        provider (audio-provider/create-lava-player-audio-provider player)
        load-handler-play (load-handler-play/load-handler player)
        load-handler-playnext (load-handler-playnext/load-handler player)]
    (.. AudioSourceManagers (registerRemoteSources player-manager))
    [player-manager player scheduler provider load-handler-play load-handler-playnext]))