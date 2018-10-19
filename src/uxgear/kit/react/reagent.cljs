(ns uxgear.kit.react.reagent
  (:require
    [uxgear.core :as ux]
    [uxgear.kit.react]
    [clojure.spec.alpha :as s]
    [reagent.core :as r]
    [reagent.ratom :as ra]))

(defn derive-reaction [reactions key f]
  ;; TODO: use key for efficiency
  (ra/make-reaction
    #(apply f (mapv deref reactions))))

(ux/defgear [:uxgear.kit.react/reactor ::reactor]
  #(ra/reactive?)
  {:ratom r/atom
   :react deref
   :derive derive-reaction})
