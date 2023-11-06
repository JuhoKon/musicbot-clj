(ns clj-music-cord.core
  (:gen-class)
  (:require [clojure.string :as str])
  (:import (com.sedmelluq.discord.lavaplayer.player DefaultAudioPlayerManager
                                                    AudioPlayer
                                                    AudioLoadResultHandler)
           (com.sedmelluq.discord.lavaplayer.track.playback MutableAudioFrame)
           (com.sedmelluq.discord.lavaplayer.source AudioSourceManagers)
           (com.sedmelluq.discord.lavaplayer.format StandardAudioDataFormats)
           (discord4j.core DiscordClientBuilder)
           (discord4j.voice AudioProvider)
           (discord4j.core.event.domain.message MessageCreateEvent)))

(def gatewaydiscordi (atom {}))

(defn clojure-fn-to-java-function [f]
  (reify java.util.function.Function
    (apply [this x]
      (f x))))

(defn to-java-consumer [provider] ;; fiksumpi tapa tehdä löytyy varmasti
  (reify java.util.function.Consumer
    (accept [this spec]
      (.setProvider spec provider))))

(defn ping [event player-manager provider scheduler]
  (let [channel (.. event (getMessage) (getChannel) (block))
        pong-message (.. channel (createMessage "pong") (block))]
    pong-message))

(defn join-voice-channel [event player-manager provider scheduler]
  (let [member (.. event (getMember) (orElse nil))
        spec-consumer (to-java-consumer provider)]
    (when member
      (let [voice-state (.. member (getVoiceState) (block))]
        (when voice-state
          (let [channel (.. voice-state (getChannel) (block))]
            (when channel
              (.block (.join channel spec-consumer)))))))))

(defn leave-voice-channel [event player-manager provider scheduler]
  (let [member (.. event (getMember) (orElse nil))]
    (when member
      (let [voice-state (.. member (getVoiceState) (block))]
        (when voice-state
          (let [channel (.. voice-state (getChannel) (block))]
            (when channel
              (.. channel (sendDisconnectVoiceState) (block)))))))))

(defn play-track [event player-manager provider scheduler]
  (let [content (.. event (getMessage) (getContent))
        url (second (str/split content #" "))]
    (.loadItem player-manager url scheduler)))

(defn create-lava-player-audio-provider [^AudioPlayer player]
  (let [buffer (java.nio.ByteBuffer/allocate (.maximumChunkSize (StandardAudioDataFormats/DISCORD_OPUS)))
        frame (MutableAudioFrame.)]
    (.setBuffer frame buffer)
    (proxy [AudioProvider] [buffer]
      (provide []
        (let [did-provide (.provide player frame)]
          (when did-provide
            (.flip buffer))
          did-provide)))))

(defn create-track-scheduler [^AudioPlayer player]
  (proxy [AudioLoadResultHandler] []
    (trackLoaded [track]
      (.playTrack player track))
    (playlistLoaded [playlist] nil)
    (noMatches [] nil)
    (loadFailed [exception] nil)))

(defn setup-audio-components []
  (let [player-manager (DefaultAudioPlayerManager.)
        player (.createPlayer player-manager)
        provider (create-lava-player-audio-provider player)]
    (.. AudioSourceManagers (registerRemoteSources player-manager))
    [player-manager player provider]))

(defn startup [bot-token]
  (-> (DiscordClientBuilder/create bot-token)
      .build
      .login
      .block))

(defn subscribe-to-message-events [player-manager provider scheduler client commands]
  (let [dispatcher (.getEventDispatcher client)]
    (-> (.on dispatcher MessageCreateEvent
             (clojure-fn-to-java-function
              (fn [event]
                (let [content (.. event (getMessage) (getContent))]
                  (doseq [[key command] commands]
                    (when (str/starts-with? content (str "!" key))
                      (command event player-manager provider scheduler)))))))
        .subscribe)))

(defn -main
  [& args]
  (let [[player-manager player provider] (setup-audio-components)
        scheduler (create-track-scheduler player)
        _ (reset! gatewaydiscordi
                  (startup "bot-token"))]
    (subscribe-to-message-events player-manager provider scheduler @gatewaydiscordi {"ping" ping
                                                                                     "join" join-voice-channel
                                                                                     "leave" leave-voice-channel
                                                                                     "play" play-track})))

(comment
  (-main)
  (.. @gatewaydiscordi (logout) (block))
  ;
  )