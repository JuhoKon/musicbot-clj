(ns clj-music-cord.audio-components.setup
  (:require [clj-music-cord.audio-components.audio-provider :as audio-provider]
            [clj-music-cord.audio-components.load-result-handler :as load-result-handler]
            [clj-music-cord.audio-components.scheduler :as scheduler])
  (:import (com.sedmelluq.discord.lavaplayer.player DefaultAudioPlayerManager)
           (com.sedmelluq.discord.lavaplayer.source AudioSourceManagers)))

(defn setup-audio-components []
  (let [player-manager (DefaultAudioPlayerManager.)
        player (.createPlayer player-manager)
        _ (.addListener player (scheduler/create-track-scheduler))
        provider (audio-provider/create-lava-player-audio-provider player)
        load-handler (load-result-handler/create-load-result-handler player)]
    (.. AudioSourceManagers (registerRemoteSources player-manager))
    [player-manager player provider load-handler]))