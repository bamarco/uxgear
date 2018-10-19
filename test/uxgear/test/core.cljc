(ns uxgear.test.core
  (:require 
    [clojure.test :refer :all]
    [clojure.spec.alpha :as s]
    [uxgear.core :as ux]))

(s/def ::msg any?)
(s/def ::other-msg any?)
(s/def ::str ifn?)

(s/def ::hello-kit (s/keys :req-un [::str ::msg]))

(ux/defscaffold h* ::hello-kit)

(ux/defscaffold o* ::hello-kit)

(s/def ::hello-entity
  (s/keys :req [::msg]))

(s/def ::different-entity
  (s/keys :req [::other-msg]))

(ux/provide [::hello-kit ::hello-entity]
  {:str str
   :msg ::msg})

(ux/provide [::hello-kit ::different-entity]
  {:str str
   :msg ::other-msg})

(deftest test-slot
  (testing "Slots"
    (let [f (ux/frame
              {:slots {:slot "hullo"
                       :+ +}})]
      (is (= "hullo" (f :slot)))
      (is (= + (f :+)))
      (is (= 0 ((f :+))))
      (is (= 1 (f :+ 1)))
      (is (= 2 (f :+ 1 1))))))

(deftest test-bind
  (testing "Bind"
    (let [world {::msg "Hello, world!"}
          other {::other-msg "A Different World!"}]
      (ux/bind [h* world
                o* other]
        (is (ux/bound? h*))
        (is (ux/bound? o*))
        (is (= "Hello, world!" (h* :msg world)))
        (is (= "Hello, world!" (h* :str (h* :msg world))))
        (is (= "A Different World!" (h* :str (o* :msg other))))
        (is (= "A Different World!" (o* :str (o* :msg other))))))))
