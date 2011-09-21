Flurfunk web frontend
=====================

A client for the Flurfunk server, written in ClojureScript.

Installing ClojureScript
------------------------

    git clone git://github.com/clojure/clojurescript.git
    cd clojurescript
    script/bootstrap

Building and running
--------------------

Execute the following:

    make

To run the client, open _flurfunk.html_ in a browser.

The above command generates minified, highly optimised JavaScript. To
make debugging easier, and the compilation process faster, execute the
following to build:

    make dev

And open _flurfunk-dev.html_ in a browser.
