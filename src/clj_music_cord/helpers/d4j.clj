(ns clj-music-cord.helpers.d4j
  (:require [clj-music-cord.helpers.d4j :as d4j-helpers]
            [clj-music-cord.shared.atoms :as atoms]))

(defn get-voice-channel [event]
  (let [member (.. event (getMember) (orElse nil))]
    (when member
      (let [voice-state (.. member (getVoiceState) (block))]
        (when voice-state
          (let [channel (.. voice-state (getChannel) (block))]
            (when channel
              channel)))))))

(defn get-text-channel [event]
  (.. event (getMessage) (getChannel) (block)))

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(defn is-bot-in-channel [voice-channel]
  (let [discord-gateway @atoms/discord-gateway-atom]
    (when (and voice-channel discord-gateway)
      (let [user-ids (map (fn [voicestate] (.. voicestate (getMember) (block) (getMemberData) (user) (id) (toString)))
                          (.. voice-channel (getVoiceStates) (collectList) (block)))
            bot-id (.. discord-gateway (getSelfId) (asString))]
        (in? user-ids bot-id)))))