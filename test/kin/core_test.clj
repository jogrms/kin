(ns kin.core-test
  (:require [clojure.test :refer :all]
            [kin.core :refer :all]
            [kin.mixins :refer :all]))

(defgen fib [x]
  (if (< x 2)
    1
    (+ (fib (dec x))
       (fib (- x 2)))))

(deftest defgen-test
  (testing "fib behaves like normal function"
    (is (= (fib 2) 2)))
  (testing "gen extracts the generator"
    (is (= ((fix (gen fib)) 3) 3))))
