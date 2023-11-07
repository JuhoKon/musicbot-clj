(ns clj-music-cord.commands.index
  (:require [clj-music-cord.commands.audio.commands :as audio-commands]
            [clj-music-cord.commands.channel.commands :as channel-commands]))

(def commands
  {"ping" channel-commands/ping
   "join" channel-commands/join-voice-channel
   "leave" channel-commands/leave-voice-channel
   "play" audio-commands/play-track
   "shuffle" audio-commands/shuffle-queue
   "nowplaying" audio-commands/now-playing
   "playnext" audio-commands/play-track-next
   "stop" audio-commands/stop-and-clear-queue
   "skip" audio-commands/skip})