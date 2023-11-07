(ns clj-music-cord.discord.event-handler
  (:require [clj-music-cord.helpers.java-helpers :as java-helpers]
            [clj-music-cord.shared.atoms :as atoms]
            [clojure.string :as str])
  (:import (discord4j.core.event.domain.message MessageCreateEvent)
           (reactor.core.publisher Mono)))

(defn subscribe-to-message-events [commands]
  (let [dispatcher (.getEventDispatcher @atoms/discord-gateway-atom)]
    (-> (.on dispatcher MessageCreateEvent
             (java-helpers/clojure-fn-to-java-function
              (fn [event]
                (let [content (.. event (getMessage) (getContent))
                      text-channel (.. event (getMessage) (getChannel) (block))]
                  (reset! atoms/current-text-channel-atom text-channel)
                  (doseq [[key command] commands]
                    (when (= (first (str/split content #" ")) (str "!" key))
                      (command event)))
                  (Mono/empty)))))
        .subscribe)))