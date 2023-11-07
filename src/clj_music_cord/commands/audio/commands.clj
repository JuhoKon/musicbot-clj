(ns clj-music-cord.commands.audio.commands
  (:require [clj-music-cord.commands.channel.commands :as channel-commands]
            [clj-music-cord.shared.atoms :as atoms]
            [clojure.string :as str]
            [clj-music-cord.helpers.queue :as queue]
            [clj-music-cord.helpers.formatters :as formatters]
            [clj-music-cord.helpers.d4j :as d4j-helpers]))

(defn is-valid-url? [str]
  (try
    (java.net.URL. str)
    true
    (catch Exception _
      false)))

(defn play-track-fn [event handler]
  (let [content (.. event getMessage getContent)
        content-parts (rest (str/split content #" "))
        url (if (is-valid-url? (apply str content-parts))
              (apply str content-parts)
              (str "ytsearch: " (str/join " " content-parts)))]
    (when-not (d4j-helpers/is-bot-in-channel)
      (channel-commands/join-voice-channel nil))
    (channel-commands/send-message-to-channel! "Loading track(s)...")
    (.. @atoms/player-manager-atom (loadItem url handler))))

(defn play-track [event]
  (play-track-fn event @atoms/load-handler-atom))

(defn play-track-next [event]
  (play-track-fn event @atoms/load-handler-atom-playnext))

(defn stop-and-clear-queue [_]
  (channel-commands/send-message-to-channel! "Stopping music and clearing queue...")
  (queue/reset-queue)
  (.. @atoms/player-atom (stopTrack)))

(defn skip [event]
  (let [next-track (first @atoms/queue-atom)]
    (channel-commands/send-message-to-channel! "Skipping current track...")
    (.. @atoms/player-atom (playTrack (first @atoms/queue-atom)))
    (queue/remove-first-from-queue!)
    (when next-track
      (channel-commands/send-message-to-channel! (str "Now playing: " (formatters/title-from-track next-track false))))))

(defn shuffle-queue [event]
  (if (empty? @atoms/queue-atom)
    (channel-commands/send-message-to-channel! "Queue is empty, won't shuffle an empty list :^)")
    (do
      (queue/shuffle-queue)
      (channel-commands/send-message-to-channel! (str "Shuffled " (count @atoms/queue-atom) " tracks!")))))

(defn now-playing [event]
  (let [track (.. @atoms/player-atom (getPlayingTrack))]
    (if track
      (channel-commands/send-message-to-channel! (str "Now playing: " (formatters/title-from-track track true)))
      (channel-commands/send-message-to-channel! "Not playing anything."))))

(defn queue-status [event]
  (if (empty?  @atoms/queue-atom)
    (channel-commands/send-message-to-channel! "The queue is empty.")
    (do
      (channel-commands/send-message-to-channel! (str "Queue has " (count @atoms/queue-atom) " tracks. Showing the next 15 tracks :^)"))
      (channel-commands/send-message-to-channel!
       (str/join "\n > " (map (fn [track] (formatters/title-from-track track false)) (take 15 @atoms/queue-atom)))))))

(defn toggle-repeat [_]
  (let [new-value (swap! atoms/repeat-mode-atom not)]
    (channel-commands/send-message-to-channel! (str "Repeat mode: " new-value))))