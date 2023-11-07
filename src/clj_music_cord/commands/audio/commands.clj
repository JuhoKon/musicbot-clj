(ns clj-music-cord.commands.audio.commands
  (:require [clj-music-cord.commands.channel.commands :as channel-commands]
            [clj-music-cord.shared.atoms :as atoms]
            [clojure.string :as str]))

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
    (channel-commands/send-message-to-channel! "Loading track...")
    (.. @atoms/player-manager-atom (loadItem url @atoms/load-handler-atom))))

(defn stop-track [event]
  (channel-commands/send-message-to-channel! "Stopping music...")
  (.. @atoms/player-atom (stopTrack)))