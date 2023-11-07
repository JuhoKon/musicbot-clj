(ns clj-music-cord.shared.atoms
  (:use [clojure.data.finger-tree :only [counted-double-list]]))

(def discord-gateway-atom (atom nil))
(def player-manager-atom (atom nil))
(def provider-atom (atom nil))
(def load-handler-atom (atom nil))
(def load-handler-atom-playnext (atom nil))
(def player-atom (atom nil))
(def track-scheduler (atom nil))

(def current-text-channel-atom (atom nil))

(def normal-queue (atom (counted-double-list)))
