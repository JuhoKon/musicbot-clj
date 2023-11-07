(ns clj-music-cord.helpers.java-helpers)

(defn clojure-fn-to-java-function [f]
  (reify java.util.function.Function
    (apply [this x]
      (f x))))

(defn to-java-consumer [provider]
  (reify java.util.function.Consumer
    (accept [this spec]
      (.setProvider spec provider))))