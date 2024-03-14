(ns clj-music-cord.discord.startup
  (:import (discord4j.core DiscordClientBuilder)))

(defn startup [bot-token]
  (-> (DiscordClientBuilder/create bot-token)
      .build
      .login
      .block))