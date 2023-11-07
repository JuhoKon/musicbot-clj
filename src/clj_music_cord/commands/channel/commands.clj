(ns clj-music-cord.commands.channel.commands
  (:require [clj-music-cord.helpers.java-helpers :as java-helpers]
            [clj-music-cord.shared.atoms :as atoms]
            [clj-music-cord.helpers.d4j :as d4j-helpers]))

(defn send-message-to-channel! [msg]
  (.. @atoms/current-text-channel-atom (createMessage (str "> " msg)) (block)))

(defn join-voice-channel [_]
  (let [spec-consumer (java-helpers/to-java-consumer @atoms/provider-atom)]
    (.. @atoms/current-voice-channel-atom (join spec-consumer) (block))))

(defn leave-voice-channel [_]
  (if (d4j-helpers/is-bot-in-channel)
    (do
      (send-message-to-channel! "Leaving...")
      (.. @atoms/current-voice-channel-atom (sendDisconnectVoiceState) (block)))
    (send-message-to-channel! "Not in a voice channel :^)")))

(defn ping [_]
  (send-message-to-channel! "pong!"))