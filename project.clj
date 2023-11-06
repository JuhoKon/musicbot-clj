(defproject clj-music-cord "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.slf4j/slf4j-simple "2.0.9"]
                 [com.discord4j/discord4j-core "3.3.0-RC1"]
                 [com.fasterxml.jackson.core/jackson-core "2.15.0"]
                 [com.sedmelluq/lavaplayer "1.3.78"]]
  :repositories {"lavaplayer-repo" "https://m2.dv8tion.net/releases"}
  :main ^:skip-aot clj-music-cord.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
