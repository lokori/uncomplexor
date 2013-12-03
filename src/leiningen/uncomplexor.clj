(ns leiningen.uncomplexor
  (:import java.io.PushbackReader)
  (:require [clojure.java.io :refer [file reader]]
            [clojure.string :refer [trim]]))

(defn files [directory path-re skip]
  (let [skip (set (map file skip))]
    (for [path (file-seq (file directory))
          :when (not (or (.isDirectory path)
                         (skip path)))
          :when (re-matches path-re (str path))]
      path)))

(defn count-nodes [data] 
  (if (seq? data) (+ 1 (reduce + 0 (map count-nodes data))) 1))

(defn branch-form? [form]
  (contains? #{'if 'if-not 'if-let 'when 'when-not 'when-let 'when-first 'case 'cond 'condp 'cond->>} form))

(defn count-branches [data] 
  (if (seq? data) 
    (reduce + 0 (map count-branches data))
  (if (branch-form? data) 1 0)))

(defn pprint-complexity [complexity-vec fname]
  (let [sum (first complexity-vec)
        nodes (second complexity-vec)
        branches (nth complexity-vec 2)]
    (str fname " has complexity " sum " (" nodes " nodes/" branches " branches)")))


(defn count-complexity [directory & {:keys [skip]
                                     :or {skip #{}}}]
  (into {}
  (apply concat
         (for [path (files directory #".*\.clj" skip)]
           (with-open [r (PushbackReader. (reader path))]
             (doall
               (for [form (repeatedly #(read r false ::eof))
                     :while (not= form ::eof)
                     :when (and (seq? form) (= 'defn (first form)))]
                 (let [nodes (count-nodes form)
                       branches (count-branches form)
		       penalty-for-branch 25
                       fname (second form)
                       id (str path ": " fname)]
                   { id [(+ nodes (* penalty-for-branch branches)) nodes  branches] }))))))))

(defn uncomplexor
  "something happens.."
  [project & args]
  (println "analyzing " (:name project))
  (let [threshold 60
        _ (println (str "functions with complexity over threshold " threshold))
        complexity-results (count-complexity  "./src")
	overly-complex (filter #(< threshold (first (second %))) complexity-results)]
	(doseq [c overly-complex]
	  (println (pprint-complexity (second c) (first c))))))

