(ns uxgear.core
  (:refer-clojure :exclude [bind bound?])
  (:require
    [clojure.spec.alpha :as s]
    [net.cgrand.macrovich :as macros]))

(defonce locker (atom {}))

(macros/deftime
  (defmacro defex [sym ex-str bindings & body]
    `(defn ~sym ~ex-str ~bindings (ex-info ~ex-str ~@body))))
    

(defn spec-ex [spec entity]
  (ex-info (s/explain-str spec entity) (s/explain-data spec entity)))

(defex missing-slots-ex
  "Unable to call frame with unbound slots"
  [frame slot args]
  {:frame frame 
   :slot slot
   :args args})

(defex missing-slot-call-ex
  "Unable to call frame without a slot argument"
  [frame slot args]
  {:frame frame
   :slot slot
   :args args})

(defex unbound-slot-ex
  "Unable to call frame with unbound slot"
  [frame slot args]
  {:frame frame
   :slot slot
   :args args})

(defn call
  "Calls a slot for the given frame passing additional args when present"
  ([frame] (call frame nil))
  ([{:as frame :keys [slots]} slot & args]
   (let [err-info (assoc frame :args args :slot slot)]
      (assert slots (throw (missing-slots-ex frame slot args)))
      (assert slot (throw  (missing-slot-call-ex frame slot args)))
      (let [f (slots slot)]
        (assert f (throw (unbound-slot-ex frame slot args)))
        (if args
          (apply f args)
          f)))))

(defn sget
  "spec-get"
  [spec->value entity]
  (some
    (fn [[spec value]]
      (when (s/valid? spec entity)
        value))
    spec->value))

(defn provide [[kit gear] slots]
  (if (s/valid? kit slots)
    (swap! locker assoc-in [kit gear] slots)
    (throw (spec-ex kit slots))))

(defn with-slots [scaffold entity]
  (let [kit (:kit scaffold)
        gear->slots (get @locker kit)]
    (assoc
      scaffold
      :slots
      (sget gear->slots entity))))

(defn bound? [scaffold]
  (-> scaffold :slots boolean))

(defrecord Frame [var slots kit]
  clojure.lang.IFn
  (invoke [this]
    (call this))
  (invoke [this slot] 
    (call this slot))
  (invoke [this slot a] 
    (call this slot a))
  (invoke [this slot a b] 
    (call this slot a b))
  (invoke [this slot a b c] 
    (call this slot a b c))
  (invoke [this slot a b c d] 
    (call this slot a b c d))
  (invoke [this slot a b c d e] 
    (call this slot a b c d e))
  (invoke [this slot a b c d e f] 
    (call this slot a b c d e f))
  (invoke [this slot a b c d e f g] 
    (call this slot a b c d e f g))
  (invoke [this slot a b c d e f g h] 
    (call this slot a b c d e f g h))
  (invoke [this slot a b c d e f g h i]
    (call this slot a b c d e f g h i))
  (invoke [this slot a b c d e f g h i j] 
    (call this slot a b c d e f g h i j))
  (invoke [this slot a b c d e f g h i j k] 
    (call this slot a b c d e f g h i j k))
  (invoke [this slot a b c d e f g h i j k l] 
    (call this slot a b c d e f g h i j k l))
  (invoke [this slot a b c d e f g h i j k l m] 
    (call this slot a b c d e f g h i j k l m))
  (invoke [this slot a b c d e f g h i j k l m n] 
    (call this slot a b c d e f g h i j k l m n))
  (invoke [this slot a b c d e f g h i j k l m n o] 
    (call this slot a b c d e f g h i j k l m n o))
  (invoke [this slot a b c d e f g h i j k l m n o p] 
    (call this slot a b c d e f g h i j k l m n o p))
  (invoke [this slot a b c d e f g h i j k l m n o p q] 
    (call this slot a b c d e f g h i j k l m n o p q))
  (invoke [this slot a b c d e f g h i j k l m n o p q r] 
    (call this slot a b c d e f g h i j k l m n o p q r))
  (invoke [this slot a b c d e f g h i j k l m n o p q r rest] 
    (apply call this slot a b c d e f g h i j k l m n o p q r rest)))

(defn frame 
  ([] (frame {}))
  ([opts] (map->Frame opts)))

(macros/deftime
  (defmacro defscaffold [sym kit]
    `(def ~sym (Frame. '~sym nil ~kit)))

  (defn kw 
    ([sym] (kw nil sym))
    ([parent sym]
     (keyword 
        (or 
          (namespace sym) 
          (str (-> *ns* ns-name name) (when parent (str "." (name parent))))) 
        (name sym))))

  (defmacro defgear [sym spec]
    `(do
        (clojure.spec.alpha/def ~(kw sym) ~spec)
        (def ~sym ~(kw sym))))

  (defmacro defkit [sym slots]
    `(do
        ~@(for [slot slots]
            `(clojure.spec.alpha/def ~(kw sym slot) ifn?))
        (clojure.spec.alpha/def ~(kw sym)
          (clojure.spec.alpha/keys :req-un ~(mapv (partial kw sym) slots)))
        (def ~sym ~(kw sym))))

  (defn scaffolding [[scaffold entity]]
    `[~(symbol (name scaffold)) (with-slots ~scaffold ~entity)])

  (defn destructure-scaffolding [bindings]
    (into
      []
      (comp
        (map scaffolding)
        cat)
      (partition 2 bindings)))

  (defmacro bind [bindings & body]
    `(let ~(destructure-scaffolding bindings) ~@body)))
