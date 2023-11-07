(ns clj-music-cord.commands.index
  (:require [clj-music-cord.commands.audio.commands :as audio-commands]
            [clj-music-cord.commands.channel.commands :as channel-commands]
            [clojure.string :as str]))

(def normal-cmds
  [{:prefix "ping" :cmd-fn channel-commands/ping :desc "Returns pong."}
   {:prefix "join" :cmd-fn channel-commands/join-voice-channel :desc "Joins your voice channel"}
   {:prefix "leave" :cmd-fn channel-commands/leave-voice-channel :desc "Leaves current voice channel"}
   {:prefix "play" :cmd-fn audio-commands/play-track :desc "Play track (from url or title). Can be playlist."}
   {:prefix "shuffle" :cmd-fn audio-commands/shuffle-queue :desc "Shuffles queue."}
   {:prefix "nowplaying" :cmd-fn audio-commands/now-playing :desc "Shows currently playing track info."}
   {:prefix "playnext" :cmd-fn audio-commands/play-track-next :desc "Play track (from url or title). Can be playlist. Adds tracks in front of the queue."}
   {:prefix "stop" :cmd-fn audio-commands/stop-and-clear-queue :desc "Stops playing music and clears queue."}
   {:prefix "skip" :cmd-fn audio-commands/skip :desc "Skips current track."}
   {:prefix "queue" :cmd-fn audio-commands/queue-status :desc "Shows next (15) tracks in the queue."}])

(defn help [_]
  (channel-commands/send-message-to-channel! "All available commands:")
  (channel-commands/send-message-to-channel!
   (str/join "\n > " (map (fn [command] (str "**!" (:prefix command) "** - " (:desc command))) normal-cmds))))

(def commands
  (conj normal-cmds {:prefix "help" :cmd-fn help :desc "Lists available commands"}))