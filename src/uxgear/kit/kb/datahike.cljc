(ns uxgear.kit.kb.datahike
  (:require
    [uxgear.core :as ux]
    [uxgear.kit.kb :as kb]
    [clojure.spec.alpha :as s]
    [datahike.core :as d]))

;; TODO: does datahike need the api modified to handle chans in cljs?

(ux/defgear db d/db?)
(ux/provide [kb/db db]
  {:q d/q
   :pull d/pull
   :entid d/entid
   :filter d/filter
   :with d/with})

(ux/defgear conn d/conn?)
(ux/provide [kb/conn conn]
  {:transact! d/transact!
   :listen!   d/listen!
   :db        d/db})
