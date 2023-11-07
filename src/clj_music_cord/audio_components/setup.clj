(ns clj-music-cord.audio-components.setup
  (:require [clj-music-cord.audio-components.audio-provider :as audio-provider]
            [clj-music-cord.audio-components.result-handler :as load-handler]
            [clj-music-cord.audio-components.scheduler :as scheduler])
  (:import (com.sedmelluq.discord.lavaplayer.player DefaultAudioPlayerManager)
           (com.sedmelluq.discord.lavaplayer.source AudioSourceManagers)))

(defn setup-audio-components []
  (let [player-manager (DefaultAudioPlayerManager.)
        player (.createPlayer player-manager)
        scheduler scheduler/track-scheduler
        _ (.addListener player scheduler)
        provider (audio-provider/create-lava-player-audio-provider player)
        load-handler-play (load-handler/load-handler player false)
        load-handler-playnext (load-handler/load-handler player true)]
    (.. AudioSourceManagers (registerRemoteSources player-manager))
    [player-manager player scheduler provider load-handler-play load-handler-playnext]))