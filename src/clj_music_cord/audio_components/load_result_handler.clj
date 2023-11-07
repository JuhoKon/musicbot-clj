(ns clj-music-cord.audio-components.load-result-handler
  (:require [clj-music-cord.commands.channel.commands :as channel-commands]
            [clj-music-cord.shared.atoms :as atoms])
  (:import (com.sedmelluq.discord.lavaplayer.player AudioLoadResultHandler AudioPlayer)))

(defn add-playlist-to-queue [tracks]
  (swap! atoms/normal-queue (reduce conj tracks)))

(defn create-load-result-handler [^AudioPlayer player]
  (proxy [AudioLoadResultHandler] []
    (trackLoaded [track]
      (let [info (.getInfo track)]
        (channel-commands/send-message-to-channel! (str "Starting playing: " (.title info)))
        (.playTrack player track)))

    (playlistLoaded [playlist]
      (let [tracks (.getTracks playlist)
            first-track (first tracks)
            info (.getInfo first-track)]
        (if (.isSearchResult playlist)
          (do
            (channel-commands/send-message-to-channel! (str "Starting playing: " (.title info)))
            (.playTrack player first-track))
          (if (.getPlayingTrack player)
            (do
              (channel-commands/send-message-to-channel! (str "Putting stuff to queue: " (count tracks)))
              (add-playlist-to-queue tracks))
            (do
              (channel-commands/send-message-to-channel! (str "Playing first song from the playlist: " (.title info)))
              (.playTrack player first-track)
              (channel-commands/send-message-to-channel! (str "Rest to queue: " (count (rest tracks))))
              (add-playlist-to-queue (rest tracks)))))))

    (noMatches []
      (channel-commands/send-message-to-channel! "No matches :(")
      (println "NO MATCHES"))

    (loadFailed [exception]
      (channel-commands/send-message-to-channel! "Load failed :(")
      (println "LOAD FAILED" exception))))