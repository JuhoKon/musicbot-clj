(ns clj-music-cord.commands.channel.commands
  (:require [clj-music-cord.helpers.java-helpers :as java-helpers]
            [clj-music-cord.shared.atoms :as atoms]
            [clj-music-cord.helpers.d4j :as d4j-helpers]))

(defn send-message-to-channel! [msg]
  (.. @atoms/current-text-channel-atom (createMessage msg) (block)))

(defn join-voice-channel [event]
  (let [spec-consumer (java-helpers/to-java-consumer @atoms/provider-atom)]
    (.. (d4j-helpers/get-voice-channel event) (join spec-consumer) (block))))

(defn leave-voice-channel [event]
  (send-message-to-channel! "Leaving...")
  (.. (d4j-helpers/get-voice-channel event) (sendDisconnectVoiceState) (block)))

(defn ping [_]
  (send-message-to-channel! "pong!"))