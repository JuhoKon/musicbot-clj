(ns clj-music-cord.commands.channel.commands
  (:require [clj-music-cord.helpers.java-helpers :as java-helpers]
            [clj-music-cord.shared.atoms :as atoms]))

(defn send-message-to-channel! [msg]
  (.. @atoms/current-text-channel-atom (createMessage (str "> " msg)) (block)))

(defn join-voice-channel [event]
  (let [spec-consumer (java-helpers/to-java-consumer @atoms/provider-atom)]
    (.. @atoms/current-voice-channel-atom (join spec-consumer) (block))))

(defn leave-voice-channel [event]
  (send-message-to-channel! "Leaving...")
  (.. @atoms/current-voice-channel-atom (sendDisconnectVoiceState) (block)))

(defn ping [_]
  (send-message-to-channel! "pong!"))