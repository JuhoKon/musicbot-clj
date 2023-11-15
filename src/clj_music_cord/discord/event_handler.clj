(ns clj-music-cord.discord.event-handler
  (:require [clj-music-cord.audio-components.setup :refer [setup-audio-components]]
            [clj-music-cord.helpers.java-helpers :as java-helpers]
            [clj-music-cord.helpers.queue :as queue]
            [clj-music-cord.shared.atoms :as atoms]
            [clojure.string :as str])
  (:import (discord4j.core.event.domain VoiceStateUpdateEvent)
           (discord4j.core.event.domain.message MessageCreateEvent)
           (reactor.core.publisher Mono)))

(defn subscribe-to-message-events [commands]
  (let [dispatcher (.getEventDispatcher @atoms/discord-gateway-atom)]
    (-> (.on dispatcher MessageCreateEvent
             (java-helpers/clojure-fn-to-java-function
              (fn [event]
                (let [content (.. event (getMessage) (getContent))
                      guild-id (-> event (.getGuildId) (.orElse nil))
                      {:keys [player player-manager scheduler provider]} (setup-audio-components guild-id)
                      queue (queue/get-queue guild-id)]
                  (doseq [{:keys [prefix cmd-fn]} commands]
                    (when (= (first (str/split content #" ")) (str "!" prefix))
                      (cmd-fn {:event event
                               :player-manager player-manager
                               :player player
                               :scheduler scheduler
                               :provider provider
                               :guild-id guild-id
                               :queue queue})))
                  (Mono/empty)))))
        .subscribe)
    (-> (.on dispatcher VoiceStateUpdateEvent
             (java-helpers/clojure-fn-to-java-function
              (fn [event]
                #_(when-let [voice-channel @atoms/current-voice-channel-atom]
                    (when (and (or (.. event (isLeaveEvent)) (.. event (isMoveEvent)))
                               (= 1 (.. voice-channel (getVoiceStates) (count) (block)))
                               (d4j-helpers/is-bot-in-channel))
                      (reset! atoms/current-voice-channel-atom nil)
                      #_(channel-commands/send-message-to-channel! "I'm left alone, so I will leave..")
                      (audio-commands/stop-and-clear-queue nil)
                      (.. voice-channel (sendDisconnectVoiceState) (block))))
                (Mono/empty))))
        .subscribe)))