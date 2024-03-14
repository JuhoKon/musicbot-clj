(ns clj-music-cord.state.guild.repeat-mode
  (:require [clj-music-cord.state.guild.state :as guild-state]))

(defn update-repeat-mode! [guild-id repeat-mode]
  (swap! guild-state/state-by-guild-id assoc-in [guild-id :repeat-mode] repeat-mode))

(defn get-repeat-mode [guild-id]
  (get-in @guild-state/state-by-guild-id [guild-id :repeat-mode]))