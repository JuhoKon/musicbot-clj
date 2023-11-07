(ns clj-music-cord.shared.atoms
  (:require [clj-music-cord.helpers.queue :as queue]))

(def discord-gateway-atom (atom nil))
(def player-manager-atom (atom nil))
(def provider-atom (atom nil))
(def load-handler-atom (atom nil))
(def player-atom (atom nil))
(def track-scheduler (atom nil))

(def current-text-channel-atom (atom nil))
(def current-voice-channel-atom (atom nil))

(def normal-queue (atom (queue/queue)))
(def priority-queue (atom (queue/queue)))