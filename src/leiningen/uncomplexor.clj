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

(defn count-complexity [directory penalty-for-branch macro-penalty & {:keys [skip]
                                     		      :or {skip #{}}}]
  (into {}
  (apply concat
         (for [path (files directory #".*\.clj" skip)]
           (with-open [r (PushbackReader. (reader path))]
             (doall
               (for [form (repeatedly #(read r false ::eof))
                     :while (not= form ::eof)
                     :when (and (seq? form) (contains? #{'defn 'defmacro} (first form)))]
                 (let [nodes (count-nodes form)
                       branches (count-branches form)
                       fname (second form)
		       multiplier (get {'defn 1 'defmacro macro-penalty} (first form))
                       id (str path ": " fname)]
                   { id [(+ nodes (* penalty-for-branch branches multiplier)) nodes  branches] }))))))))

(def default-opts {:threshold 60
     		   :branch-penalty 30
		   :macro-penalty 2
		   :source-dir "./src"})

(defn uncomplexor
  "running complexity analysis.."
  [project & args]
  (println "analyzing " (:name project))
  (let [opts (merge default-opts (:uncomplexor project))
       threshold (:threshold opts)
       branch-penalty (:branch-penalty opts)
       macro-penalty (:macro-penalty opts)
       source-dir (:source-dir opts)
        _ (println (str "functions or macros with complexity over threshold " threshold))
        complexity-results (count-complexity source-dir branch-penalty macro-penalty)
	overly-complex (filter #(< threshold (first (second %))) complexity-results)]
	
	(doseq [c overly-complex]
	  (println (pprint-complexity (second c) (first c))))))

