(ns clj-music-cord.state.guild.init
  (:require [clj-music-cord.audio-components.setup :as audio-setup]
            [clj-music-cord.state.guild.state :as guild-state]
            [clojure.data.finger-tree :as finger-tree]))

(defn init-guild-state! [guild-id]
  (when guild-id
    (or (guild-state/get-state-by-guild-id guild-id)
        (let [{:keys [player player-manager scheduler provider]} (audio-setup/setup-audio-components guild-id)
              queue (finger-tree/counted-double-list)
              repeat-mode false
              state {:player player
                     :player-manager player-manager
                     :scheduler scheduler
                     :provider provider
                     :queue queue
                     :repeat-mode repeat-mode}]
          (guild-state/update-state-by-guild-id! guild-id state)
          state))))