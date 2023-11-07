(ns clj-music-cord.discord.event-handler
  (:require [clj-music-cord.helpers.java-helpers :as java-helpers]
            [clj-music-cord.shared.atoms :as atoms]
            [clj-music-cord.helpers.d4j :as d4j-helpers]
            [clojure.string :as str])
  (:import (discord4j.core.event.domain.message MessageCreateEvent)
           (discord4j.core.event.domain VoiceStateUpdateEvent)
           (reactor.core.publisher Mono)))

(defn subscribe-to-message-events [commands]
  (let [dispatcher (.getEventDispatcher @atoms/discord-gateway-atom)]
    (-> (.on dispatcher MessageCreateEvent
             (java-helpers/clojure-fn-to-java-function
              (fn [event]
                (let [content (.. event (getMessage) (getContent))
                      text-channel (.. event (getMessage) (getChannel) (block))
                      voice-channel (d4j-helpers/get-voice-channel event)]
                  (reset! atoms/current-text-channel-atom text-channel)
                  (reset! atoms/current-voice-channel-atom voice-channel)
                  (doseq [[key command] commands]
                    (when (= (first (str/split content #" ")) (str "!" key))
                      (command event)))
                  (Mono/empty)))))
        .subscribe)
    (-> (.on dispatcher VoiceStateUpdateEvent
             (java-helpers/clojure-fn-to-java-function
              (fn [event]
                (let [voice-channel @atoms/current-voice-channel-atom]
                  (when (and (or (.. event (isLeaveEvent)) (.. event (isMoveEvent))) (= 1 (.. voice-channel (getVoiceStates) (count) (block))))
                    (.. voice-channel (sendDisconnectVoiceState) (block)))
                  (Mono/empty)))))
        .subscribe)))
