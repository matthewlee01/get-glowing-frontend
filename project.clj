(defproject archon "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.6"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [cljs-ajax "0.8.0"]
                 [clj-commons/cljss "1.6.4"]
                 [cljsjs/auth0-lock "11.8.0-0"]
                 [bidi "2.1.6"]
                 [kibu/pushy "0.3.8"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]]

    :plugins      [[lein-figwheel "0.5.18"]]}
   :prod {}}
   

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "archon.core/mount-root"}
     :compiler     {:main                 archon.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}}}
                    

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            archon.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})


    
  
