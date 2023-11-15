(ns clj-music-cord.commands.channel.commands
  (:require [clj-music-cord.helpers.java-helpers :as java-helpers]
            [clj-music-cord.helpers.d4j :as d4j-helpers]))

(defn send-message-to-channel! [event msg]
  (.. (d4j-helpers/get-text-channel event) (createMessage (str "> " msg)) (block)))

(defn join-voice-channel [{:keys [event provider]}]
  (let [spec-consumer (java-helpers/to-java-consumer provider)]
    (.. (d4j-helpers/get-voice-channel event) (join spec-consumer) (block))))

(defn leave-voice-channel [{:keys [event]}]
  (if (d4j-helpers/is-bot-in-channel (d4j-helpers/get-voice-channel event))
    (do
      (send-message-to-channel! event "Leaving...")
      (.. (d4j-helpers/get-voice-channel event) (sendDisconnectVoiceState) (block)))
    (send-message-to-channel! event "Not in a voice channel :^)")))

(defn ping [{:keys [event]}]
  (send-message-to-channel! event "pong!"))