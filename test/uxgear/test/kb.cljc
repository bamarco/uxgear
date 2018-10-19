(ns uxgear.test.kb
  (:require 
    [clojure.test :refer :all]
    [uxgear.core :as ux]
    [datascript.core :as ds]
    [datahike.core :as dh]
    [datomic.api   :as da]
    [uxgear.kit.kb :as kb]
    [uxgear.kit.kb.datascript]
    [uxgear.kit.kb.datahike]
    [uxgear.kit.kb.datomic :as kb.da]))
  
(ux/defscaffold d* kb/db)
(ux/defscaffold c* kb/conn)

(defn pull [kb expr eid]
  (ux/bind [d* kb
            c* kb]
    (cond
      (ux/bound? d*)
      (d* :pull kb expr eid)
    
      (ux/bound? c*)
      (pull (c* :db kb) expr eid))))

(def schema {::name {:db/unique :db.unique/identity}})

(def da-schema [{:db/ident       ::name
                 :db/valueType   :db.type/keyword
                 :db/cardinality :db.cardinality/one
                 :db/unique      :db.unique/identity}
                {:db/ident       :msg
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/one}])

(def txs [{::name ::hello :msg "Hello, world!"}])

(deftest test-kb
  (testing "KB"
    (let [ds-conn (ds/create-conn schema)
          dh-conn (dh/create-conn schema)
          da-conn (kb.da/create-conn)]
      (ds/transact! ds-conn txs)
      (dh/transact! dh-conn txs)
      (da/transact da-conn da-schema)
      (da/transact da-conn txs)
      (testing "Datascript"
        (is (= "Hello, world!" (:msg (ds/pull @ds-conn [:msg] [::name ::hello]))))
        (is (= "Hello, world!" (:msg (pull ds-conn [:msg] [::name ::hello]))))
        (is (= "Hello, world!" (:msg (pull @ds-conn [:msg] [::name ::hello])))))
      (testing "Datahike"
        (is (= "Hello, world!" (:msg (dh/pull @dh-conn [:msg] [::name ::hello]))))
        (is (= "Hello, world!" (:msg (pull dh-conn [:msg] [::name ::hello]))))
        (is (= "Hello, world!" (:msg (pull @dh-conn [:msg] [::name ::hello])))))
      (testing "Datomic"
        (is (= "Hello, world!" (:msg (da/pull (da/db da-conn) [:msg] [::name ::hello]))))
        (is (= "Hello, world!" (:msg (pull da-conn [:msg] [::name ::hello]))))
        (is (= "Hello, world!" (:msg (pull (da/db da-conn) [:msg] [::name ::hello]))))))))

    

