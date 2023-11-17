(ns clj-music-cord.commands.index
  (:require [clj-music-cord.commands.audio.commands :as audio-commands]
            [clj-music-cord.commands.channel.commands :as channel-commands]
            [clj-music-cord.helpers.d4j :as d4j-helpers]
            [clojure.string :as str]))

(defn in-voice-channel [cmd-fn]
  (fn [{:keys [event] :as args}]
    (if (d4j-helpers/get-voice-channel event)
      (cmd-fn args)
      (channel-commands/send-message-to-channel! event "You have to be in a voice channel to use this command."))))

(def normal-cmds
  [{:prefix "ping" :cmd-fn channel-commands/ping :desc "Returns pong."}
   {:prefix "join" :cmd-fn (in-voice-channel channel-commands/join-voice-channel) :desc "Joins your voice channel"}
   {:prefix "leave" :cmd-fn channel-commands/leave-voice-channel :desc "Leaves current voice channel"}
   {:prefix "play" :cmd-fn (in-voice-channel audio-commands/play-track) :desc "Play track (from url or title). Can be playlist."}
   {:prefix "shuffle" :cmd-fn audio-commands/shuffle-queue :desc "Shuffles queue."}
   {:prefix "nowplaying" :cmd-fn audio-commands/now-playing :desc "Shows currently playing track info."}
   {:prefix "playnext" :cmd-fn (in-voice-channel audio-commands/play-track-next) :desc "Play track (from url or title). Can be playlist. Adds tracks in front of the queue."}
   {:prefix "stop" :cmd-fn audio-commands/stop-and-clear-queue :desc "Stops playing music and clears queue."}
   {:prefix "skip" :cmd-fn (in-voice-channel audio-commands/skip) :desc "Skips current track."}
   {:prefix "queue" :cmd-fn audio-commands/queue-status :desc "Shows next (15) tracks in the queue."}
   {:prefix "repeat" :cmd-fn audio-commands/toggle-repeat :desc "Repeats the queue."}
   {:prefix "volume" :cmd-fn audio-commands/get-volume :desc "Gets the current volume for the player."}
   {:prefix "setvolume" :cmd-fn audio-commands/set-volume :desc "Sets the current volume for the player (0-150)."}])

(defn help [{:keys [event]}]
  (channel-commands/send-message-to-channel! event "All available commands:")
  (channel-commands/send-message-to-channel! event
                                             (str/join "\n > " (map (fn [command] (str "**!" (:prefix command) "** - " (:desc command))) normal-cmds))))

(def commands
  (conj normal-cmds {:prefix "help" :cmd-fn help :desc "Lists available commands"}))