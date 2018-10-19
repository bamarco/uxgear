(ns uxgear.kit.kb.datomic
  (:require
    [uxgear.core :as ux]
    [uxgear.kit.kb :as kb]
    [clojure.spec.alpha :as s]
    [datomic.api :as d]))

(defn db? [db]
  (isa? (type db) datomic.db.Db))

(defn conn? [conn]
  (isa? (type conn) datomic.Connection))

(defn listen! [conn kw listener])
  ;; TODO: listener for datomic
  ;; ???: async chan option?

(defn create-conn
  ([] (create-conn "datomic:mem://base"))
  ([url & {:as options :keys [keep?] :or {keep? false}}]
   (when-not keep?
      (d/delete-database url))
   (d/create-database url)
   (d/connect url)))

(ux/defgear db db?)
(ux/provide [kb/db db]
  {:q d/q
   :pull d/pull
   :entid d/entid
   :with d/with
   :filter d/filter})
   

(ux/defgear conn conn?)
(ux/provide [kb/conn conn]
  {:transact! d/transact
   :listen! listen!
   :db d/db})
