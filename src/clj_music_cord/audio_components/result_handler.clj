(ns clj-music-cord.audio-components.result-handler
  (:require [clj-music-cord.commands.channel.commands :as channel-commands]
            [clj-music-cord.helpers.formatters :as formatters]
            [clj-music-cord.helpers.queue :as queue])
  (:import (com.sedmelluq.discord.lavaplayer.player AudioLoadResultHandler AudioPlayer)))

(defn load-handler [^AudioPlayer player add-to-front? event guild-id]
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
            (channel-commands/send-message-to-channel! event (str "Adding to the the queue: " (formatters/title-from-track track)))
            (add-song-to-queue guild-id track))
          (do
            (channel-commands/send-message-to-channel! event (str "Start playing: " (formatters/title-from-track track)))
            (.startTrack player track true))))

      (playlistLoaded [playlist]
        (let [tracks (.getTracks playlist)
              first-track (first tracks)]
          (if (.isSearchResult playlist)
            (if (.getPlayingTrack player)
              (do
                (channel-commands/send-message-to-channel! event (str "Adding to the the queue: " (formatters/title-from-track first-track)))
                (add-song-to-queue guild-id first-track))
              (do
                (channel-commands/send-message-to-channel! event (str "Start playing: " (formatters/title-from-track first-track)))
                (.startTrack player first-track true)))
            (if (.getPlayingTrack player)
              (do
                (channel-commands/send-message-to-channel! event (str "Inserting tracks to the queue: " (count tracks)))
                (add-playlist-to-queue guild-id tracks))
              (do
                (channel-commands/send-message-to-channel! event (str "Playing first song from the playlist: " (formatters/title-from-track first-track)))
                (.startTrack player first-track true)
                (channel-commands/send-message-to-channel! event (str "Add rest of the playlist to queue: " (count (rest tracks))))
                (add-playlist-to-queue guild-id (rest tracks)))))))

      (noMatches []
        (channel-commands/send-message-to-channel! event "No matches :(")
        (println "NO MATCHES"))

      (loadFailed [exception]
        (channel-commands/send-message-to-channel! event "Load failed :(")
        (println "LOAD FAILED" exception)))))