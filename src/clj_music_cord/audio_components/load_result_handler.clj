(ns clj-music-cord.audio-components.load-result-handler
  (:require [clj-music-cord.commands.channel.commands :as channel-commands])
  (:import (com.sedmelluq.discord.lavaplayer.player AudioLoadResultHandler AudioPlayer)))

(defn create-load-result-handler [^AudioPlayer player]
  (proxy [AudioLoadResultHandler] []
    (trackLoaded [track]
      (let [info (.getInfo track)]
        (channel-commands/send-message-to-channel! (str "Starting playing: " (.title info)))
        (.playTrack player track)))
    (playlistLoaded [playlist] ;; handle actual playlist load. Implement queue?
      (when (.isSearchResult playlist)
        (let [tracks (.getTracks playlist)
              first-track (first tracks)
              info (.getInfo first-track)]
          (channel-commands/send-message-to-channel! (str "Starting playing: " (.title info)))
          (.playTrack player first-track))))
    (noMatches []
      (channel-commands/send-message-to-channel! "No matches :(")
      (println "NO MATCHES"))
    (loadFailed [exception]
      (channel-commands/send-message-to-channel! "Load failed :(")
      (println "LOAD FAILED" exception))))