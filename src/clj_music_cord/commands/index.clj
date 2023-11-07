(ns clj-music-cord.commands.index
  (:require [clj-music-cord.commands.audio.commands :as audio-commands]
            [clj-music-cord.commands.channel.commands :as channel-commands]))

(def commands
  {"ping" channel-commands/ping
   "join" channel-commands/join-voice-channel
   "leave" channel-commands/leave-voice-channel
   "play" audio-commands/play-track
   "stop" audio-commands/stop-track})