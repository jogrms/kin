(ns kin.mixins)

(defn trace
  "Traces calls. Prints the supplied name along with arguments."
  [name]
  (fn [self]
    (fn [& args]
      (prn (cons name args))
      (apply self args))))

(defn memo
  "Caches return values."
  []
  (let [cache (atom (hash-map))]
    (fn [self]
      (fn [& args]
        (if-let [e (find @cache args)]
          (val e)
          (let [res (apply self args)]
            (swap! cache assoc args res)
            res))))))
