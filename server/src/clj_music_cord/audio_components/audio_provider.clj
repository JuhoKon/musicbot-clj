(ns clj-music-cord.audio-components.audio-provider
  (:import (com.sedmelluq.discord.lavaplayer.format StandardAudioDataFormats)
           (com.sedmelluq.discord.lavaplayer.player  AudioPlayer)
           (com.sedmelluq.discord.lavaplayer.track.playback MutableAudioFrame)
           (discord4j.voice AudioProvider)))

(defn create-lava-player-audio-provider [^AudioPlayer player]
  (let [buffer (java.nio.ByteBuffer/allocate (.maximumChunkSize (StandardAudioDataFormats/DISCORD_OPUS)))
        frame (MutableAudioFrame.)]
    (.setBuffer frame buffer)
    (proxy [AudioProvider] [buffer]
      (provide []
        (let [did-provide (.provide player frame)]
          (when did-provide
            (.flip buffer))
          did-provide)))))