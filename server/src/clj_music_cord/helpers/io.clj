(ns clj-music-cord.helpers.io
  (:require [clojure.java.io :as io]))

(defn read-text-from-file [file-path]
  (with-open [reader (io/reader file-path)]
    (apply str (line-seq reader))))