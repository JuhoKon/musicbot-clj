(ns clj-music-cord.commands.audio.commands
  (:require [clj-music-cord.commands.channel.commands :as channel-commands]
            [clj-music-cord.shared.atoms :as atoms]
            [clojure.string :as str]
            [clj-music-cord.helpers.queue :as queue]
            [clj-music-cord.helpers.formatters :as formatters]))

(defn is-valid-url? [str]
  (try
    (java.net.URL. str)
    true
    (catch Exception _
      false)))

(defn play-track [event]
  (let [content (.. event getMessage getContent)
        content-parts (rest (str/split content #" "))
        url (if (is-valid-url? (apply str content-parts))
              (apply str content-parts)
              (str "ytsearch: " (str/join " " content-parts)))]
    (channel-commands/send-message-to-channel! "Loading track(s)...")
    (.. @atoms/player-manager-atom (loadItem url @atoms/load-handler-atom))))

(defn stop-and-clear-queue [_]
  (channel-commands/send-message-to-channel! "Stopping music and clearing queue...")
  (queue/reset-queue)
  (.. @atoms/player-atom (stopTrack)))

(defn play-track-next [event]
  (let [content (.. event getMessage getContent)
        content-parts (rest (str/split content #" "))
        url (if (is-valid-url? (apply str content-parts))
              (apply str content-parts)
              (str "ytsearch: " (str/join " " content-parts)))]
    (channel-commands/send-message-to-channel! "Loading track(s)...")
    (.. @atoms/player-manager-atom (loadItem url @atoms/load-handler-atom-playnext))))

(defn skip [event]
  (channel-commands/send-message-to-channel! "Skipping current track...")
  (.. @atoms/player-atom (playTrack (first @atoms/normal-queue)))
  (queue/remove-first-from-queue!))

(defn shuffle-queue [event]
  (if (empty? @atoms/normal-queue)
    (channel-commands/send-message-to-channel! "Queue is empty, won't shuffle an empty list :^)")
    (do
      (queue/shuffle-queue)
      (channel-commands/send-message-to-channel! (str "Shuffled " (count @atoms/normal-queue) " tracks!")))))

(defn now-playing [event]
  (let [track (.. @atoms/player-atom (getPlayingTrack))]
    (if track
      (channel-commands/send-message-to-channel! (str "Now playing: " (formatters/title-from-info (.. track (getInfo)) true)))
      (channel-commands/send-message-to-channel! "Not playing anything."))))