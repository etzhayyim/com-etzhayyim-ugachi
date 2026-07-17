#!/usr/bin/env bb
(require '[babashka.classpath :as cp]
         '[babashka.fs :as fs]
         '[clojure.test :as t])

(def repo-root (fs/parent (fs/absolutize *file*)))
(defn dependency-src [environment sibling]
  (or (System/getenv environment)
      (str (fs/parent repo-root) "/" sibling "/src")))

(doseq [path [(str repo-root)
              (dependency-src "IE_FLOW_SRC" "com-etzhayyim-ie-flow")
              (dependency-src "KOTOBA_DATOM_SRC" "com-etzhayyim-kotoba-datom")]]
  (cp/add-classpath path))

(def suites
  '[ugachi.methods.test-ugachi-edn
    ugachi.methods.test-gate
    ugachi.methods.test-bridge
    ugachi.methods.test-kotoba
    ugachi.methods.test-autorun
    ugachi.methods.test-ie-flow
    ugachi.methods.test-refusal-drivers])

(apply require suites)
(let [{:keys [fail error]} (apply t/run-tests suites)]
  (System/exit (if (zero? (+ fail error)) 0 1)))
