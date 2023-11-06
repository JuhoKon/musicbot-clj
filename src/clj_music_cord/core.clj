(ns clj-music-cord.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (com.sedmelluq.discord.lavaplayer.format StandardAudioDataFormats)
           (com.sedmelluq.discord.lavaplayer.player AudioLoadResultHandler AudioPlayer DefaultAudioPlayerManager)
           (com.sedmelluq.discord.lavaplayer.source AudioSourceManagers)
           (com.sedmelluq.discord.lavaplayer.track.playback MutableAudioFrame)
           (discord4j.core DiscordClientBuilder)
           (discord4j.core.event.domain.message MessageCreateEvent)
           (discord4j.voice AudioProvider)))


(defn clojure-fn-to-java-function [f]
  (reify java.util.function.Function
    (apply [this x]
      (f x))))

(defn to-java-consumer [provider]
  (reify java.util.function.Consumer
    (accept [this spec]
      (.setProvider spec provider))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def discord-gateway-atom (atom nil))
(def player-manager-atom (atom nil))
(def provider-atom (atom nil))
(def scheduler-atom (atom nil))
(def player-atom (atom nil))
(def current-text-channel-atom (atom nil))
(def current-voice-channel-atom (atom nil))

(defn send-message-to-channel! [msg]
  (.. @current-text-channel-atom (createMessage msg) (block)))

(defn ping [event]
  (send-message-to-channel! "pong!"))

(defn join-voice-channel [event]
  (let [spec-consumer (to-java-consumer @provider-atom)]
    (.. @current-voice-channel-atom (join spec-consumer) (block))))

(defn leave-voice-channel [event]
  (.. @current-voice-channel-atom (sendDisconnectVoiceState) (block)))

(defn is-valid-url? [str]
  (try
    (java.net.URL. str)
    true
    (catch Exception e
      false)))

(defn play-track [event]
  (let [content (.. event (getMessage) (getContent))
        url (if (is-valid-url? content)
              content
              (str "ytsearch: " (str/join " " (rest (str/split content #" ")))))]
    (send-message-to-channel! "Loading track...")
    (.. @player-manager-atom (loadItem url @scheduler-atom))))

(defn stop-track [event]
  (send-message-to-channel! "Stopping music...")
  (.. @player-atom (stopTrack)))

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

(defn get-voice-channel [event]
  (let [member (.. event (getMember) (orElse nil))]
    (when member
      (let [voice-state (.. member (getVoiceState) (block))]
        (when voice-state
          (let [channel (.. voice-state (getChannel) (block))]
            (when channel
              channel)))))))

(defn subscribe-to-message-events [commands]
  (let [dispatcher (.getEventDispatcher @discord-gateway-atom)]
    (-> (.on dispatcher MessageCreateEvent ;; TODO handle bot leaving and joining voice channel -> keep status in an atom
             (clojure-fn-to-java-function
              (fn [event]
                (let [content (.. event (getMessage) (getContent))
                      text-channel (.. event (getMessage) (getChannel) (block))
                      voice-channel (get-voice-channel event)]
                  (reset! current-text-channel-atom text-channel)
                  (reset! current-voice-channel-atom voice-channel)
                  (doseq [[key command] commands]
                    (when (str/starts-with? content (str "!" key))
                      (command event)))))))
        .subscribe)))

(def commands
  {"ping" ping
   "join" join-voice-channel
   "leave" leave-voice-channel
   "play" play-track
   "stop" stop-track})

(defn create-track-scheduler [^AudioPlayer player]
  (proxy [AudioLoadResultHandler] []
    (trackLoaded [track]
      (let [info (.getInfo track)]
        (send-message-to-channel! (str "Starting playing: " (.title info)))
        (.playTrack player track)))
    (playlistLoaded [playlist] ;; handle actual playlist load. Implement queue?
      (when (.isSearchResult playlist)
        (let [tracks (.getTracks playlist)
              first-track (first tracks)
              info (.getInfo first-track)]
          (send-message-to-channel! (str "Starting playing: " (.title info)))
          (.playTrack player first-track))))
    (noMatches []
      (send-message-to-channel! "No matches :(")
      (println "NO MATCHES"))
    (loadFailed [exception]
      (send-message-to-channel! "Load failed :(")
      (println "LOAD FAILED" exception))))

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

(defn read-text-from-file [file-path]
  (with-open [reader (io/reader file-path)]
    (apply str (line-seq reader))))

(defn -main
  [& args]
  (let [[player-manager player provider] (setup-audio-components)
        scheduler (create-track-scheduler player)
        token (str (read-text-from-file "token.txt"))]

    (reset! discord-gateway-atom (startup token))
    (reset! player-manager-atom player-manager)
    (reset! player-atom player)
    (reset! provider-atom provider)
    (reset! scheduler-atom scheduler)

    (subscribe-to-message-events commands)))

(comment
  (-main)
  (.. @discord-gateway-atom (logout) (block))
  ;
  )