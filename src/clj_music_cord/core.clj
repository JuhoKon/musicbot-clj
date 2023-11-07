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
  (let [audio-components (setup-audio-components)
        token (str (read-text-from-file "token.txt"))
        client (discord-startup/startup token)]

    (apply atoms/set-atoms! (conj audio-components client))
    (discord-event-handler/subscribe-to-message-events commands/commands)
    (-> client
        (.onDisconnect)
        (.block))))

(comment
  (-main)
  ;
  )