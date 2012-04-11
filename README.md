Flurfunk web frontend
=====================

A client for the Flurfunk server, written in ClojureScript and built using
Leiningen.

Building and running
--------------------

### Installing Leiningen 2 ###

    curl -O https://raw.github.com/technomancy/leiningen/preview/bin/lein
    chmod +x lein
    mv lein ~/bin/ # Make sure that ~/bin/ exists and is on the $PATH

### Development mode ###

    lein cljsbuild auto
    lein ring server-headless
    
Then go to http://localhost:3000/dev.

### Production mode ###

    lein cljsbuild once
    lein ring server

This will try to connect to a server running at
http://localhost:4000. If you're server is running somewhere else, you
need to set the _flurfunk.server.uri_ system property:

    JAVA_OPTS="-Dflurfunk.server.uri=http://localhost:1337" lein ring server

### Mobile style ###

Flurfunk comes with a style optimised for mobile devices. You can see
it in production mode on http://localhost:3000/mobile and in
development mode on http://localhost:3000/mobile/dev.

### Creating a WAR ###

    mkdir temp
    lein ring uberwar temp/flurfunk-web.war

When running in an application server like Tomcat as non-root app
(i.e. with a context path), flurfunk-server is expected at the context
path _/flurfunk-server_ in the same application server. You can
overwrite this by setting the system property _flurfunk.server.uri_.

License
-------

Copyright 2012 Viaboxx GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
