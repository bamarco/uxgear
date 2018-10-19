(ns uxgear.kit.kb
  (:require
    [uxgear.core :as ux]))

;; TODO: db is being spec'd twice.
;;   Solutions:
;;     - use a sperate namespace
;;     - provide error when it occurs
;;     - more complex macro
;;     - use more complex declaration

(ux/defkit db [q pull entid filter with])

(ux/defkit conn [db transact! listen!])