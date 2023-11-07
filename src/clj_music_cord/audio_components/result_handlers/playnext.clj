(ns clj-music-cord.audio-components.result-handlers.playnext
  (:require [clj-music-cord.commands.channel.commands :as channel-commands]
            [clj-music-cord.shared.atoms :as atoms]
            [clj-music-cord.helpers.queue :as queue])
  (:import (com.sedmelluq.discord.lavaplayer.player AudioLoadResultHandler AudioPlayer)))

(defn load-handler [^AudioPlayer player]
  (proxy [AudioLoadResultHandler] []
    (trackLoaded [track]
      (let [info (.getInfo track)]
        (channel-commands/send-message-to-channel! (str "Starting playing: " (.title info)))
        (if (.getPlayingTrack player)
          (do
            (channel-commands/send-message-to-channel! (str "Adding to the top of the queue: " (.title info)))
            (queue/add-song-to-queue-front track))
          (do
            (channel-commands/send-message-to-channel! (str "Starting playing: " (.title info)))
            (.startTrack player track true)))))

    (playlistLoaded [playlist]
      (let [tracks (.getTracks playlist)
            first-track (first tracks)
            info (.getInfo first-track)]
        (if (.isSearchResult playlist)
          (do
            (if (.getPlayingTrack player)
              (do
                (channel-commands/send-message-to-channel! (str "Adding to the top of the queue: " (.title info)))
                (queue/add-song-to-queue-front first-track))
              (do
                (channel-commands/send-message-to-channel! (str "Starting playing: " (.title info)))
                (.startTrack player first-track true))))
          (if (.getPlayingTrack player)
            (do
              (channel-commands/send-message-to-channel! (str "Putting stuff to the queue: " (count tracks)))
              (queue/add-playlist-to-queue-front tracks))
            (do
              (channel-commands/send-message-to-channel! (str "Playing first song from the playlist: " (.title info)))
              (.startTrack player first-track true)
              (channel-commands/send-message-to-channel! (str "Rest to the queue: " (count (rest tracks))))
              (queue/add-playlist-to-queue-front (rest tracks)))))))

    (noMatches []
      (channel-commands/send-message-to-channel! "No matches :(")
      (println "NO MATCHES"))

    (loadFailed [exception]
      (channel-commands/send-message-to-channel! "Load failed :(")
      (println "LOAD FAILED" exception))))