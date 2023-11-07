(ns clj-music-cord.commands.audio.commands
  (:require [clj-music-cord.commands.channel.commands :as channel-commands]
            [clj-music-cord.shared.atoms :as atoms]
            [clojure.string :as str]))

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
              content
              (str "ytsearch: " (str/join " " content-parts)))]
    (channel-commands/send-message-to-channel! url)
    (channel-commands/send-message-to-channel! "Loading track...")
    (.. @atoms/player-manager-atom (loadItem url @atoms/load-handler-atom))))

(defn stop-track [_]
  (channel-commands/send-message-to-channel! "Stopping music...")
  (.. @atoms/player-atom (stopTrack)))

;; For playnext just provide different loadhandler?
;; start using startTrack. has flag for interrupting

;; skip? is essentially playnext from the queue
;; (.playTrack player (get-song-from-queue))