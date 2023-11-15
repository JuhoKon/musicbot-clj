(ns clj-music-cord.discord.event-handler
  (:require [clj-music-cord.helpers.d4j :as d4j-helpers]
            [clj-music-cord.helpers.java-helpers :as java-helpers]
            [clj-music-cord.state.global :as global]
            [clj-music-cord.state.guild.queue :as queue]
            [clj-music-cord.state.guild.init :as guild-state]
            [clojure.string :as str])
  (:import (discord4j.core.event.domain VoiceStateUpdateEvent)
           (discord4j.core.event.domain.message MessageCreateEvent)
           (reactor.core.publisher Mono)))

(defn subscribe-to-message-events [commands]
  (let [dispatcher (.getEventDispatcher @global/discord-gateway-atom)]
    (-> (.on dispatcher MessageCreateEvent
             (java-helpers/clojure-fn-to-java-function
              (fn [event]
                (let [content (.. event (getMessage) (getContent))
                      guild-id (-> event (.getGuildId) (.orElse nil))
                      {:keys [player player-manager scheduler provider queue repeat-mode]} (guild-state/init-guild-state! guild-id)]
                  (doseq [{:keys [prefix cmd-fn]} commands]
                    (when (= (first (str/split content #" ")) (str "!" prefix))
                      (cmd-fn {:event event
                               :player-manager player-manager
                               :player player
                               :scheduler scheduler
                               :provider provider
                               :guild-id guild-id
                               :queue queue
                               :repeat-mode repeat-mode})))
                  (Mono/empty)))))
        .subscribe)
    (-> (.on dispatcher VoiceStateUpdateEvent
             (java-helpers/clojure-fn-to-java-function
              (fn [event]
                (let [voice-state (.. event (getOld) (orElse nil))]
                  (when voice-state
                    (let [channel (.. voice-state (getChannel) (block))
                          guild-id (.. voice-state (getGuildId))
                          {:keys [player]} (guild-state/init-guild-state! guild-id)]
                      (when channel
                        (when (and (or (.. event (isLeaveEvent)) (.. event (isMoveEvent)))
                                   (= 1 (.. channel (getVoiceStates) (count) (block)))
                                   (d4j-helpers/is-bot-in-channel channel))
                          (queue/reset-queue guild-id)
                          (.. player (stopTrack))
                          (.. channel (sendDisconnectVoiceState) (block)))
                        channel))))
                (Mono/empty))))
        .subscribe)))