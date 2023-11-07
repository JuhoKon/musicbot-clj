(ns clj-music-cord.audio-components.result-handler
  (:require [clj-music-cord.commands.channel.commands :as channel-commands]
            [clj-music-cord.shared.atoms :as atoms]
            [clj-music-cord.helpers.formatters :as formatters]
            [clj-music-cord.helpers.queue :as queue])
  (:import (com.sedmelluq.discord.lavaplayer.player AudioLoadResultHandler AudioPlayer)))

(defn load-handler [^AudioPlayer player add-to-front?]
  (let [add-song-to-queue (if add-to-front?
                            queue/add-song-to-queue-front
                            queue/add-song-to-queue)
        add-playlist-to-queue (if add-to-front?
                                queue/add-playlist-to-queue-front
                                queue/add-playlist-to-queue)]
    (proxy [AudioLoadResultHandler] []
      (trackLoaded [track]
        (if (.getPlayingTrack player)
          (do
            (channel-commands/send-message-to-channel! (str "Adding to the the queue: " (formatters/title-from-track track)))
            (add-song-to-queue track))
          (do
            (channel-commands/send-message-to-channel! (str "Start playing: " (formatters/title-from-track track)))
            (.startTrack player track true))))

      (playlistLoaded [playlist]
        (let [tracks (.getTracks playlist)
              first-track (first tracks)]
          (if (.isSearchResult playlist)
            (do
              (if (.getPlayingTrack player)
                (do
                  (channel-commands/send-message-to-channel! (str "Adding to the the queue: " (formatters/title-from-track first-track)))
                  (add-song-to-queue first-track))
                (do
                  (channel-commands/send-message-to-channel! (str "Start playing: " (formatters/title-from-track first-track)))
                  (.startTrack player first-track true))))
            (if (.getPlayingTrack player)
              (do
                (channel-commands/send-message-to-channel! (str "Inserting tracks to the queue: " (count tracks)))
                (add-playlist-to-queue tracks))
              (do
                (channel-commands/send-message-to-channel! (str "Playing first song from the playlist: " (formatters/title-from-track first-track)))
                (.startTrack player first-track true)
                (channel-commands/send-message-to-channel! (str "Add rest of the playlist to queue: " (count (rest tracks))))
                (add-playlist-to-queue (rest tracks)))))))

      (noMatches []
        (channel-commands/send-message-to-channel! "No matches :(")
        (println "NO MATCHES"))

      (loadFailed [exception]
        (channel-commands/send-message-to-channel! "Load failed :(")
        (println "LOAD FAILED" exception)))))