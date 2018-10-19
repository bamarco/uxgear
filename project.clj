(defproject uxgear "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :doo {:build "test"
        :alias {:default [:chrome-headless]}}
  :cljsbuild {:builds
              [{:id "test"
                :source-paths ["src"]
                :compiler {:output-to "out/js/compiled_test/onyx_sim_test.js"
                           :output-dir "out/js/compiled_test/out"
                           :main uxgear.test.runner
                           :optimizations :none}}]}
  :profiles {:dev {:plugins [[lein-doo "0.1.8"]]}}
  :dependencies [[org.clojure/clojure "1.10.0-alpha4"]
                 [org.clojure/clojurescript "1.10.238"]
                 [net.cgrand/macrovich "0.2.1"]
                 [datascript "0.16.6"]
                 [io.replikativ/datahike "0.1.2"]
                 [com.datomic/datomic-free "0.9.5697"]])
                 
