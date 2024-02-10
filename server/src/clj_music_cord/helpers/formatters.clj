(ns clj-music-cord.helpers.formatters)

(defn format-milliseconds [milliseconds]
  (let [total-seconds (/ milliseconds 1000)
        hours (int (quot total-seconds 3600))
        minutes (int (mod (quot total-seconds 60) 60))
        seconds (int (mod total-seconds 60))]
    (if (> hours 0)
      (str (format "%02d" hours) ":" (format "%02d" minutes) ":" (format "%02d" seconds))
      (str (format "%02d" minutes) ":" (format "%02d" seconds)))))

(defn title-from-track
  ([track] (title-from-track track false))
  ([track include-uri?]
   (let [info (.getInfo track)
         title (.title info)
         ms (.length info)
         uri (.uri info)]
     (if include-uri?
       (str (format "**%s** `%s` [Link](%s)" title (format-milliseconds ms) uri))
       (str (format "**%s** `%s`" title (format-milliseconds ms)))))))