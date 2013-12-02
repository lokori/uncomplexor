# uncomplexor

Experimental Leiningen plugin to measure complexity of Clojure code.

## Usage

Put `[uncomplexor "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your project.clj.

### example run on the plugin itself

$ lein uncomplexor

```
analyzing  uncomplexor
functions with complexity over threshold 60
./src/leiningen/uncomplexor.clj: count-branches has complexity 74 (24 nodes/2 branches)
```


## What does it do?

This is an experiment on measuring complexity of Clojure code. Hopefully it is somewhat useful.  It mimics a classic measurement of cyclomatic complexity. Uncomplexor calculates a complexity number based on a simple formula:

complexity = number of nodes + 25 * number of branch-nodes

Thus, a longer function with a lot of nodes (which are called forms in Clojure) gets a bigger number. And having multiple branching forms (such as if or when) means a bigger number too. 

## Is it useful? 

No idea yet. It seems to work, though it is not perfectly accurate. Macros are not counted. 
Most Clojure code seems to be well written at the moment as I personally see things. 

Well written code in any language does not benefit from analysis of this sort. How it will work out as Clojure becomes more popular remains to be seen. People had been talking about this sort of thing in the internet earlier so I decided to give it a shoot.


## TODO

Things are now cast in stone. Should have some parameters for complexity threshold, scanned files, excluded files etc.

## License

Copyright © 2013 Antti Virtanen

(I am fairly certain that I have borrowed a few lines of code from Timo Mihaljov, the part that reads files recursively.)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
