(defproject org.clojars.lokori/uncomplexor "0.1.0"
  :description "Code complexity measurement for Clojure"
  :url "https://github.com/lokori/uncomplexor"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm "https://github.com/lokori/uncomplexor"
  :signing {:gpg-key "antti.virtanen@iki.fi"}
  :eval-in-leiningen true
  :uncomplexor {:threshold 65
  	        :branch-penalty 30
		:macro-penalty 2})
