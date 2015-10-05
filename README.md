# kin
A simple implementation of the function inheritance mechanism described in the
[paper](http://www.cs.utexas.edu/users/wcook/Drafts/2009/sblp09-memo-mixins.pdf)
by Daniel Brown and William R. Cook.

Traditionally, an unrelated quote:

> A great doom awaits you, either to rise above the height of all your
> fathers since the days of Elendil, or to fall into darkness with all that
> is left of your kin. Many years of trial lie before you.

— *Lord Elrond*

## Installation

[![Clojars Project](http://clojars.org/kin/latest-version.svg)](http://clojars.org/kin)

## Usage

```clojure
(use 'kin.core)
(use 'kin.mixins)
```

Suppose that you are trying to define a recursive version of fib and apply
memoization to it.
You can start by defining a recursive fib generator using the `defgen` macro:

```clojure
(defgen fib [x]
  (if (< x 2)
    1
    (+ (fib (dec x))
       (fib (- x 2)))))
```

`defgen` uses the same syntax as `defn` but produces a generator along with the function.
There is also the `fgen` macro which substitutes `fn` in a similar way.
You can use `fib` as a regular function:

```clojure
(map fib (range 6))
;; => (1 1 2 3 5 8)
```

Or you can extract the generator using the `gen` function and compute the fixed point using `fix`:

```clojure
((-> fib gen fix) 4)
;; => 5
```

Now let's define a version of fib that traces all the calls and parameters using the
`trace` mixin:

```clojure
(def fib-trace (inherit fib (trace 'fib)))

(fib-trace 4)
;; (fib 4)
;; (fib 3)
;; (fib 2)
;; (fib 1)
;; (fib 0)
;; (fib 1)
;; (fib 2)
;; (fib 1)
;; (fib 0)
;; => 5
```

And memoization using the `memo` mixin:

```clojure
(def fib-trace-memo (inherit fib-trace (memo)))
;; or
(def fib-trace-memo* (inherit fib (trace 'fib) (memo)))

(fib-trace-memo 4)
;; (fib 4)
;; (fib 3)
;; (fib 2)
;; (fib 1)
;; (fib 0)
;; => 5

(fib-trace-memo 4)
;; => 5
```

## Writing your own mixins
Suppose that you want to implement some custom logger that logs all calls to a function
and stores them in a vector:
```clojure
(defn logger [l]
  (fn [self]
    (fn [& args]
      (swap! l conj (str "args: " args))
      (apply self args))))
```
Let's try it:
```clojure
(def log (atom []))
(def fib-logger (inherit fib (logger log)))
(fib-logger 4)
;; => 5
(pprint @log)
;; ["args: (4)"
;;  "args: (3)"
;;  "args: (2)"
;;  "args: (1)"
;;  "args: (0)"
;;  "args: (1)"
;;  "args: (2)"
;;  "args: (1)"
;;  "args: (0)"]
```
