(ns clj-music-cord.state.guild.state)

(def state-by-guild-id (atom {}))

(defn update-state-by-guild-id! [guild-id components]
  (swap! @state-by-guild-id assoc guild-id components))

(defn get-state-by-guild-id [guild-id]
  (get @state-by-guild-id guild-id))