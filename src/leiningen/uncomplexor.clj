(ns leiningen.uncomplexor
  (:import java.io.PushbackReader)
  (:require [clojure.java.io :refer [file reader]]
            [clojure.string :refer [trim]]))

(defn tiedostot [hakemisto polku-re ohita]
  (let [ohita (set (map file ohita))]
    (for [polku (file-seq (file hakemisto))
          :when (not (or (.isDirectory polku)
                         (ohita polku)))
          :when (re-matches polku-re (str polku))]
      polku)))

(defn count-nodes [data] 
  (if (seq? data) (+ 1 (reduce + 0 (map count-nodes data))) 1))

(defn branch-form? [form]
  (contains? #{'if 'when 'if-let 'when-not 'when-let} form))

(defn count-branches [data] 
  (if (seq? data) 
    (reduce + 0 (map count-branches data))
  (if (branch-form? data) 1 0)))

(defn pprint-kompleksisuus [kompleksisuus-vec fname]
  (let [sum (first kompleksisuus-vec)
        nodes (second kompleksisuus-vec)
        branches (nth kompleksisuus-vec 2)]
    (str fname " has complexity " sum " (" nodes " nodes/" branches " branches)")))


(defn count-complexity [hakemisto & {:keys [ohita]
                                     :or {ohita #{}}}]
  (into {}
  (apply concat
         (for [polku (tiedostot hakemisto #".*\.clj" ohita)]
           (with-open [r (PushbackReader. (reader polku))]
             (doall
               (for [form (repeatedly #(read r false ::eof))
                     :while (not= form ::eof)
                     :when (and (seq? form) (= 'defn (first form)))]
                 (let [nodes (count-nodes form)
                       branches (count-branches form)
		       penalty-for-branch 25
                       fname (second form)
                       id (str  polku ": " fname)]
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
	  (println (pprint-kompleksisuus (second c) (first c))))))

