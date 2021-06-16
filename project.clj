(defproject cljs-spirolaterals "0.1.0-SNAPSHOT"
  :description "A ClojureScript program for drawing spirolaterals"
  :url "https://github.com/harold/cljs-spirolaterals"
  :license {:name "Copyright 2021, Harold"
            :url "https://github.com/harold/cljs-spirolaterals"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.597"]
                 [com.bhauman/figwheel-main "0.2.4"]
                 [com.bhauman/rebel-readline-cljs "0.1.4"]
                 [reagent "0.10.0"]]
  :plugins [[lein-pprint "1.3.2"]]
  :resource-paths ["target" "resources"]
  :aliases {"build-prod" ["trampoline" "run" "-m" "figwheel.main" "-bo" "prod"]}
  :clean-targets ^{:protect false} [:target-path])
