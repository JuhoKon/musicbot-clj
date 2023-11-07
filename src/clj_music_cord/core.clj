(ns clj-music-cord.core
  (:gen-class)
  (:require [clj-music-cord.audio-components.setup :refer [setup-audio-components]]
            [clj-music-cord.commands.index :as commands]
            [clj-music-cord.discord.startup :as discord-startup]
            [clj-music-cord.discord.event-handler :as discord-event-handler]
            [clj-music-cord.shared.atoms :as atoms]
            [clojure.java.io :as io]))

(defn read-text-from-file [file-path]
  (with-open [reader (io/reader file-path)]
    (apply str (line-seq reader))))

(defn -main
  [& _]
  (let [[player-manager player scheduler provider load-handler-play load-handler-playnext] (setup-audio-components)
        token (str (read-text-from-file "token.txt"))]

    (reset! atoms/discord-gateway-atom (discord-startup/startup token))
    (reset! atoms/player-manager-atom player-manager)
    (reset! atoms/player-atom player)
    (reset! atoms/track-scheduler scheduler)
    (reset! atoms/provider-atom provider)
    (reset! atoms/load-handler-atom load-handler-play)
    (reset! atoms/load-handler-atom-playnext load-handler-playnext)
    (discord-event-handler/subscribe-to-message-events commands/commands)))

(comment
  (-main)
  (.. @atoms/discord-gateway-atom (logout) (block))
  ;
  )