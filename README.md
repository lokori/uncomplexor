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

function declaration complexity = number of nodes + 30 * number of branch-nodes
macro declaration complexity = 2 * (number of nodes + 30 * number of branch-nodes)

Thus, a longer function with a lot of nodes (which are called forms in Clojure) gets a bigger number. And having multiple branching forms (such as if or when) means a bigger number too. This correlation makes common sense but the number is just a number. 

### What is complexity?

The number is supposed to measure the complexity of the code from the human point of view. More specifically, it is an estimate of how much effort is needed to understand the runtime behaviour (design) of the code block without executing it. Macro expansions do not count in this regard as the very point of using a macro is to provide a new abstraction which makes it easier for humans to understand the code.

While using a macro is easy, writing and understanding a macro declaration is not. They are more difficult to reason about than pure functions. 


## Is it useful? 

No idea yet. It seems to work, though it is not perfectly accurate. Ad hoc functions declared with # or fn are not given any consideration etc. so the "real complexity" might be hidden. But measuring complexity can never be accurate anyway.

Most Clojure code seems to be well written at the moment as I personally see things.  Well written code in any language does not benefit from analysis of this sort. How it will work out as Clojure becomes more popular remains to be seen. People had been talking about this sort of thing in the internet earlier so I decided to give it a try.


## License

Copyright © 2013 Antti Virtanen

(I am fairly certain that I have borrowed a few lines of code from Timo Mihaljov, the part that reads files recursively.)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
