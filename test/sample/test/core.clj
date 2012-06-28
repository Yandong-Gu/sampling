;; Copyright (c) 2011, 2012 BigML, Inc
;; All rights reserved.

;; Author: Adam Ashenfelter <ashenfad@bigml.com>
;; Start date: Jun 27, 2012

(ns sample.test.core
  (:use clojure.test)
  (:require (sample [core :as core]
                    [reservoir :as reservoir]
                    [stream :as stream])))

(defn- about-eq
  "Returns true if the absolute value of the difference
   between the first two arguments is less than the third."
  [v1 v2 tol]
  (< (Math/abs (double (- v1 v2))) tol))

(deftest simple-sample
  (is (about-eq (reduce + (take 500 (core/sample (range 1000))))
                250000 25000))
  (is (about-eq (reduce + (take 500 (core/sample (range 1000) :replace true)))
                250000 25000))
  (let [[v1 v2] (vals (frequencies (take 1000 (core/sample [0 1] :replace true))))]
    (is (about-eq v1 v2 150))))

(deftest reservoir-sample
  (is (about-eq (reduce + (reservoir/sample (range 1000) 500))
                250000 25000))
  (is (about-eq (reduce + (reservoir/sample (range 1000) 500 :replace true))
                250000 25000))
  (is (= (reservoir/sample (range 20) 10 :seed 7)
         (reduce reservoir/insert
                 (reservoir/create 10 :seed 7)
                 (range 20))))
  (is (= (reservoir/sample (range 20) 10 :seed 7 :replace true)
         (reduce reservoir/insert
                 (reservoir/create 10 :seed 7 :replace true)
                 (range 20)))))

(deftest stream-sample
  (is (about-eq (reduce + (stream/sample (range 1000) 500 1000))
                250000 25000))
  (is (about-eq (reduce + (stream/sample (range 1000) 500 1000 :replace true))
                250000 25000))
  (is (about-eq (reduce + (stream/sample (range 1000) 500 1000
                                         :replace true
                                         :approximate true))
                250000 25000)))

(deftest regression
  (is (= (take 10 (core/sample (range 20) :seed 7))
         '(16 13 17 12 9 4 18 7 14 19)))
  (is (= (take 10 (core/sample (range 20) :seed 7 :replace true))
         '(16 4 5 4 0 14 8 9 10 14)))
  (is (= (reservoir/sample (range 20) 10 :seed 7)
         [13 1 14 4 16 3 7 5 17 15]))
  (is (= (reservoir/sample (range 20) 10 :seed 7 :replace true)
         [8 19 11 16 19 0 10 18 9 6]))
  (is (= (stream/sample (range 20) 10 20 :seed 7)
         '(3 4 5 7 10 12 14 15 16 17)))
  (is (= (stream/sample (range 20) 10 20 :seed 7 :replace true)
         '(3 5 8 10 11 12 13 14 17 19)))
  (is (= (stream/sample (range 20) 10 20 :seed 7 :replace true :approximate true)
         '(0 1 3 4 7 9 9 10 11 16 19))))