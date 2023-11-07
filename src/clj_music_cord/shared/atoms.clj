(ns clj-music-cord.shared.atoms
  (:use [clojure.data.finger-tree :only [counted-double-list]]))

(def discord-gateway-atom (atom nil))
(def player-manager-atom (atom nil))
(def provider-atom (atom nil))
(def load-handler-atom (atom nil))
(def load-handler-atom-playnext (atom nil))
(def player-atom (atom nil))
(def track-scheduler-atom (atom nil))

(def current-text-channel-atom (atom nil))
(def current-voice-channel-atom (atom nil))

(def queue-atom (atom (counted-double-list)))

(def repeat-mode-atom (atom false))

;; Helper

(defn set-atoms!
  [player-manager player scheduler provider load-handler load-handler-playnext client]
  (reset! player-manager-atom player-manager)
  (reset! player-atom player)
  (reset! track-scheduler-atom scheduler)
  (reset! provider-atom provider)
  (reset! load-handler-atom load-handler)
  (reset! load-handler-atom-playnext load-handler-playnext)
  (reset! discord-gateway-atom client))