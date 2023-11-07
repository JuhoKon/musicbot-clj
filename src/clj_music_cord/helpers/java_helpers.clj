(ns clj-music-cord.helpers.java-helpers)

(defn clojure-fn-to-java-function [f]
  (reify java.util.function.Function
    (apply [_ x]
      (f x))))

(defn to-java-consumer [provider]
  (reify java.util.function.Consumer
    (accept [_ spec]
      (.setProvider spec provider))))