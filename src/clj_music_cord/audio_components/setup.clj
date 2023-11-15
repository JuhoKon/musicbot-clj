(ns clj-music-cord.audio-components.setup
  (:require [clj-music-cord.audio-components.audio-provider :as audio-provider]
            [clj-music-cord.audio-components.scheduler :as scheduler]
            [clojure.data.finger-tree :as finger-tree]
            [clj-music-cord.helpers.queue :refer [update-queue!]])
  (:import (com.sedmelluq.discord.lavaplayer.player DefaultAudioPlayerManager)
           (com.sedmelluq.discord.lavaplayer.source AudioSourceManagers)))

(def audio-components-by-guild-id (atom {}))

(defn update-audio-components! [guild-id components]
  (swap! audio-components-by-guild-id assoc guild-id components))

(defn get-audio-components [guild-id]
  (get @audio-components-by-guild-id guild-id))

(defn setup-audio-components [guild-id]
  (when guild-id
    (or (get-audio-components guild-id)
        (let [player-manager (DefaultAudioPlayerManager.)
              player (.createPlayer player-manager)
              scheduler (scheduler/track-scheduler guild-id)
              provider (audio-provider/create-lava-player-audio-provider player)
              audio-components {:player-manager player-manager
                                :player player
                                :scheduler scheduler
                                :provider provider}]
          (update-queue! guild-id (finger-tree/counted-double-list)) ; Init queue
          (.addListener player scheduler)
          (.. AudioSourceManagers (registerRemoteSources player-manager))
          (update-audio-components! guild-id audio-components)
          audio-components))))