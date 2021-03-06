;  Copyright (c) Dave Ray, 2011. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this 
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns seesaw.test.table
  (:use seesaw.table)
  (:use [lazytest.describe :only (describe it testing given)]
        [lazytest.expect :only (expect)]))

(describe table-model
  (it "should create a table model"
    (instance? javax.swing.table.TableModel (table-model)))

  (it "should create columns from a list of keys"
    (let [t (table-model :columns [:key1 :key2])]
      (expect (= "key1" (.getColumnName t 0)))
      (expect (= "key2" (.getColumnName t 1)))))

  (it "should create columns from a list of maps and keys"
    (let [t (table-model :columns [{:key :key1 :text "KEY1"} :key2])]
      (expect (= "KEY1" (.getColumnName t 0)))
      (expect (= "key2" (.getColumnName t 1)))))

  (it "should create rows from a list of maps"
    (let [t (table-model :columns [:a :b] :rows [{:a "a0" :b "b0"} {:a "a1" :b "b1"}])]
      (expect (= 2 (.getRowCount t)))
      (expect (= "a0" (.getValueAt t 0 0)))
      (expect (= "b0" (.getValueAt t 0 1)))
      (expect (= "a1" (.getValueAt t 1 0)))
      (expect (= "b1" (.getValueAt t 1 1)))))

  (it "should create row from a map with extraneous fields without crashing"
    (let [t (table-model :columns [:a] :rows [{:a "a0" :b "b0"}])]
      (expect (= "a0" (.getValueAt t 0 0)))))
          
  (it "should create rows from a list of vectors"
    (let [t (table-model :columns [:a :b] :rows [["a0" "b0"] ["a1" "b1"]])]
      (expect (= 2 (.getRowCount t)))
      (expect (= "a0" (.getValueAt t 0 0)))
      (expect (= "b0" (.getValueAt t 0 1)))
      (expect (= "a1" (.getValueAt t 1 0)))
      (expect (= "b1" (.getValueAt t 1 1)))))
  
  (it "makes column metadata available through (.getValueAt model -1 -1)"
    (let [t (table-model :columns [:a :b])]
      (expect (= 1 (:b (.getValueAt t -1 -1))))))

  (it "returns false for isCellEditable"
    (let [t (table-model :columns [:a :b] :rows [[0 0]])]
      (expect (not (.isCellEditable t 0 0))))))

(describe value-at
  (it "gets the value of a single row index as a map"
    (let [t (table-model :columns [:a :b] :rows [["a0" "b0"] ["a1" "b1"]])]
      (expect (= {:a "a0" :b "b0" } (value-at t 0)))))
  (it "gets the value of a row as a map (indexed by integers) if model was not
      created with (table-model)"
    (let [t (javax.swing.table.DefaultTableModel. 2 3)]
      (expect (= {"A" nil "B" nil "C" nil } (value-at t 0)))))
  (it "gets the value of a sequence of row indices as a list of maps"
    (let [t (table-model :columns [:a :b] :rows [["a0" "b0"] ["a1" "b1"]])]
      (expect (= [{:a "a0" :b "b0" } {:a "a1" :b "b1" }] (value-at t [0 1]))))))

(describe update-at!
  (it "updates a row with the same format as :rows option of (table-model)"
    (let [t (table-model :columns [:a :b] :rows [["a0" "b0"] ["a1" "b1"]])
          r (update-at! t 0 ["A0" "B0"])]
      (expect (= t r))
      (expect (= {:a "A0" :b "B0"} (value-at t 0)))))
  (it "updates a only the columns specified in a row"
    (let [t (table-model :columns [:a :b] :rows [["a0" "b0"] ["a1" "b1"]])
          r (update-at! t 0 {:a "A0"})]
      (expect (= t r))
      (expect (= {:a "A0" :b "b0"} (value-at t 0)))))
  (it "updates multiple rows with the same format as :rows option of (table-model)"
    (let [t (table-model :columns [:a :b] :rows [["a0" "b0"] ["a1" "b1"]])
          r (update-at! t 1 ["A1" "B1"] 0 {:a "A0" :b "B0"})]
      (expect (= t r))
      (expect (= {:a "A0" :b "B0"} (value-at t 0))))
      (expect (= {:a "A1" :b "B1"} (value-at t 1)))))

(describe insert-at!
  (it "inserts a row with the same format as :rows option of (table-model)"
    (let [t (table-model :columns [:a :b] :rows [["a0" "b0"] ["a1" "b1"]])
          r (insert-at! t 0 ["A0" "B0"])]
      (expect (= t r))
      (expect (= 3 (.getRowCount t)))
      (expect (= {:a "A0" :b "B0"} (value-at t 0)))
      (expect (= {:a "a0" :b "b0"} (value-at t 1)))))
  (it "inserts multiple rows with the same format as :rows option of (table-model)"
    (let [t (table-model :columns [:a] :rows (map vector (range 5)))
          r (insert-at! t 1 ["A"] 3 ["B"])]
      (expect (= t r))
      (expect (= 7 (.getRowCount t)))
      (expect (= [{:a 0} {:a "A"} {:a 1} {:a 2} {:a "B"} {:a 3} {:a 4}] (value-at t (range (.getRowCount t))))))))

(describe remove-at!
  (it "removes a row"
    (let [t (table-model :columns [:a] :rows (map vector (range 5)))
          r (remove-at! t 2 )]
      (expect (= t r))
      (expect (= 4 (.getRowCount t)))))
  (it "removes multiple rows, assuming that they are sorted!"
    (let [t (table-model :columns [:a] :rows (map vector (range 5)))
          r (remove-at! t 1 2 3)]
      (expect (= t r))
      (expect (= 2 (.getRowCount t)))
      (expect (= [{:a 0} {:a 4}] (value-at t [0 1]))))))

(describe clear!
  (it "removes all rows from a table"
    (let [t (table-model :columns [:a] :rows (map vector (range 5)))
          r (clear! t)]
      (expect (= r t))
      (expect (= 0 (.getRowCount t))))))

(describe row-count
  (it "retrievies number of rows in a table"
    (let [t (table-model :columns [:a] :rows (map vector (range 5)))]
      (expect (= 5 (row-count t))))))

(describe column-count
  (it "retrievies number of columns in a table"
    (let [t (table-model :columns [:a :b :c :d])]
      (expect (= 4 (column-count t))))))
