(ns clj-music-cord.discord.event-handler
  (:require [clj-music-cord.helpers.d4j :as d4j-helpers]
            [clj-music-cord.helpers.java-helpers :as java-helpers]
            [clj-music-cord.shared.atoms :as atoms]
            [clojure.string :as str])
  (:import (discord4j.core.event.domain.message MessageCreateEvent)))

(defn subscribe-to-message-events [commands]
  (let [dispatcher (.getEventDispatcher @atoms/discord-gateway-atom)]
    (-> (.on dispatcher MessageCreateEvent ;; TODO handle bot leaving and joining voice channel -> keep status in an atom
             (java-helpers/clojure-fn-to-java-function
              (fn [event]
                (let [content (.. event (getMessage) (getContent))
                      text-channel (.. event (getMessage) (getChannel) (block))
                      voice-channel (d4j-helpers/get-voice-channel event)]
                  (reset! atoms/current-text-channel-atom text-channel)
                  (reset! atoms/current-voice-channel-atom voice-channel)
                  (doseq [[key command] commands]
                    (when (str/starts-with? content (str "!" key))
                      (command event)))))))
        .subscribe)))