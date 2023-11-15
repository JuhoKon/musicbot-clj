(ns clj-music-cord.shared.atoms)

(def discord-gateway-atom (atom nil))

(def repeat-mode-by-guild-id (atom {}))

(defn update-repeat-mode! [guild-id repeat-mode]
  (swap! repeat-mode-by-guild-id assoc guild-id repeat-mode))

(defn get-repeat-mode [guild-id]
  (get @repeat-mode-by-guild-id guild-id))


;; Helper

(defn set-atoms! [client]
  (reset! discord-gateway-atom client))