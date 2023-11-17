(ns clj-music-cord.core
  (:gen-class)
  (:require [clj-music-cord.commands.index :as commands]
            [clj-music-cord.discord.event-handler :as discord-event-handler]
            [clj-music-cord.discord.startup :as discord-startup]
            [clj-music-cord.helpers.io :as io-helpers]
            [clj-music-cord.state.global :as global]))

(defn -main
  [& _]
  (let [token (str (io-helpers/read-text-from-file "token.txt"))
        client (discord-startup/startup token)]

    (global/set-atoms! client)
    (discord-event-handler/subscribe-to-message-events commands/commands)
    #_(-> client
          (.onDisconnect)
          (.block))))

(comment
  (-main)
  ;
  )