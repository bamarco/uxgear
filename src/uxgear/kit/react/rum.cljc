(ns uxgear.kit.react.rum
  (:require
    [uxgear.core :as ux]
    [uxgear.kit.react :as react]
    [clojure.spec.alpha :as s]
    [rum.core :as r]))

(ux/provide [:uxgear.kit.react/reactor ::reactor]
  {:ratom atom
   :react r/react
   :derive r/derived-atom})

; (defmethod react/reactor-type ::reactor [_]
;   (s/keys :req [:uxgear.kit.react/reactor-type]))

; (s/def ::reactor :uxgear.kit.react/reactor)
