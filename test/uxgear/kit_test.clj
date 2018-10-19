(ns uxgear.kit-test
  (:require 
    [clojure.test :refer :all]
    [clojure.spec.alpha :as s]
    [uxgear.core :as ux]))

(ux/defkit kit [slot])

(s/def ::derive fn?)
(s/def ::left-slot any?)
(s/def ::right-slot any?)

(ux/defgear slotted-gear (s/and (s/keys :req [::left-slot]) #(not (::derive %))))

(ux/defgear derived-gear (s/keys :req [::derive ::left-slot ::right-slot]))

(ux/provide [kit derived-gear]
  {:slot #(let [{::keys [derive left-slot right-slot]} %]
            (derive left-slot right-slot))})

(ux/provide [kit slotted-gear]
  {:slot ::left-slot})

(ux/defscaffold k* kit)

(deftest test-kit
  (testing "Kit"
      (is (s/valid? :uxgear.kit-test.kit/slot str))
      (is (s/valid? ::kit {:slot str}))))

(deftest test-gear
  (testing "Gear"
    (let [slotted {::left-slot "green"}
          derived {::derive + ::left-slot 1 ::right-slot 1}]
      (ux/bind [k* slotted]
        (is (= (k* :slot) ::left-slot))
        (is (= (k* :slot slotted) "green")))
      (ux/bind [k* derived]
        (is (fn? (k* :slot)))
        (is (= (k* :slot derived) 2))))))
  
    
