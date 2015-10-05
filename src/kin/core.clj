(ns kin.core
  (:require [clojure.walk :refer [postwalk macroexpand-all]]))

;; Macros

(defn- self-transformer [name self]
  (fn [form] (if (= form name) self form)))

(defn- get-generator [sigs]
  (if-not (symbol? (first sigs))
    `(fn [self#] (fn ~@sigs))
    (let [[name args & exprs] sigs
          self (gensym "self")]
      `(fn [~self]
         (fn ~args ~@(map #(postwalk (self-transformer name self) %)
                          exprs))))))

(defmacro fgen
  "Create a generator using fn syntax"
  [& sigs]
  `(with-meta (fn ~@sigs)
     {:generator ~(get-generator sigs)}))

(defmacro defgen
  "Declare a generator using defn syntax"
  [name & sigs]
  `(def ~name (fgen ~name ~@sigs)))

;; Functions

(defn fix [f]
  "Fixed-point combinator."
  (fn [& args] (apply (f (fix f)) args)))

(defn gen [v]
  "Extract the generator function from a value created using fgen or defgen."
  (-> v meta :generator))

(defn inherit [f & mixins]
  "Apply mixins to f."
  (let [generator (apply comp (reverse (cons (gen f) mixins)))]
    (with-meta (fix generator) {:generator generator})))
