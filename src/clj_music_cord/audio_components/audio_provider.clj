(ns clj-music-cord.audio-components.audio-provider
  (:import (com.sedmelluq.discord.lavaplayer.format StandardAudioDataFormats)
           (com.sedmelluq.discord.lavaplayer.player AudioLoadResultHandler AudioPlayer DefaultAudioPlayerManager)
           (com.sedmelluq.discord.lavaplayer.player.event AudioEventAdapter)
           (com.sedmelluq.discord.lavaplayer.source AudioSourceManagers)
           (com.sedmelluq.discord.lavaplayer.track.playback MutableAudioFrame)
           (com.sedmelluq.discord.lavaplayer.track AudioTrackEndReason)
           (discord4j.core DiscordClientBuilder)
           (discord4j.core.event.domain.message MessageCreateEvent)
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