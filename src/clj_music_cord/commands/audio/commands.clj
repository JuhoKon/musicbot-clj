(ns clj-music-cord.commands.audio.commands
  (:require [clj-music-cord.commands.channel.commands :as channel-commands]
            [clj-music-cord.state.guild.repeat-mode :as repeat-mode]
            [clojure.string :as str]
            [clj-music-cord.state.guild.queue :as queue]
            [clj-music-cord.helpers.formatters :as formatters]
            [clj-music-cord.audio-components.result-handler :as load-handler]
            [clj-music-cord.helpers.d4j :as d4j-helpers]))

(defn is-valid-url? [str]
  (try
    (java.net.URL. str)
    true
    (catch Exception _
      false)))

(defn play-track-fn [event handler player-manager provider]
  (let [content (.. event getMessage getContent)
        content-parts (rest (str/split content #" "))
        url (if (is-valid-url? (apply str content-parts))
              (apply str content-parts)
              (str "ytsearch: " (str/join " " content-parts)))]
    (when-not (d4j-helpers/is-bot-in-channel (d4j-helpers/get-voice-channel event))
      (channel-commands/join-voice-channel {:event event :provider provider}))
    (channel-commands/send-message-to-channel! event "Loading track(s)...")
    (.. player-manager (loadItem url handler))))

(defn play-track [{:keys [event player provider player-manager guild-id]}]
  (play-track-fn event (load-handler/load-handler player false event guild-id) player-manager provider))

(defn play-track-next [{:keys [event player provider player-manager guild-id]}]
  (play-track-fn event (load-handler/load-handler player true event guild-id) player-manager provider))

(defn stop-and-clear-queue [{:keys [event player guild-id]}]
  (channel-commands/send-message-to-channel! event "Stopping music and clearing queue...")
  (queue/reset-queue guild-id)
  (.. player (stopTrack)))

(defn skip [{:keys [event player guild-id queue]}]
  (let [next-track (first queue)]
    (channel-commands/send-message-to-channel! event "Skipping current track...")
    (.. player (playTrack (first queue)))
    (queue/remove-first-from-queue! guild-id)
    (when next-track
      (channel-commands/send-message-to-channel! event (str "Now playing: " (formatters/title-from-track next-track false))))))

(defn shuffle-queue [{:keys [event guild-id queue]}]
  (if (empty? queue)
    (channel-commands/send-message-to-channel! event "Queue is empty, won't shuffle an empty list :^)")
    (do
      (queue/shuffle-queue guild-id)
      (channel-commands/send-message-to-channel! event (str "Shuffled " (count queue) " tracks!")))))

(defn now-playing [{:keys [event player]}]
  (let [track (.. player (getPlayingTrack))]
    (if track
      (channel-commands/send-message-to-channel! event (str "Now playing: " (formatters/title-from-track track true)))
      (channel-commands/send-message-to-channel! event "Not playing anything."))))

(defn queue-status [{:keys [event queue]}]
  (if (empty? queue)
    (channel-commands/send-message-to-channel! event "The queue is empty.")
    (do
      (channel-commands/send-message-to-channel! event (str "Queue has " (count queue) " tracks. Showing the next 15 tracks if available :^)"))
      (channel-commands/send-message-to-channel! event
                                                 (str/join "\n > " (map (fn [track] (formatters/title-from-track track false)) (take 15 queue)))))))

(defn toggle-repeat [{:keys [event guild-id repeat-mode]}]
  (let [old-value repeat-mode
        _ (repeat-mode/update-repeat-mode! guild-id (not old-value))]
    (channel-commands/send-message-to-channel! event (str "Repeat mode: " repeat-mode))))