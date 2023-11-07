(ns clj-music-cord.helpers.d4j)

(defn get-voice-channel [event]
  (let [member (.. event (getMember) (orElse nil))]
    (when member
      (let [voice-state (.. member (getVoiceState) (block))]
        (when voice-state
          (let [channel (.. voice-state (getChannel) (block))]
            (when channel
              channel)))))))