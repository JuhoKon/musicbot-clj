(ns clj-music-cord.state.global)

(def discord-gateway-atom (atom nil))

(defn set-atoms! [client]
  (reset! discord-gateway-atom client))