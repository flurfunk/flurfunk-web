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

This will simulate a Flurfunk server in-browser. If you want to
connect to a real one, tick the _Use real server_ checkbox. The URL is
set as in production mode, see below.

### Production mode ###

    lein cljsbuild once
    lein ring server

This will try to connect to a server running at
http://localhost:4000. If your server is running somewhere else, you
need to set the _flurfunk.server_ system property:

    JAVA_OPTS="-Dflurfunk.server=http://localhost:1337" lein ring server

### Mobile style ###

Flurfunk comes with a style optimised for mobile devices. You can see
it in production mode on http://localhost:3000/mobile and in
development mode on http://localhost:3000/mobile/dev.

### Creating a WAR ###

    lein ring uberwar flurfunk-web.war

This will create _target/flurfunk-web.war_.

When running in an application server like Tomcat as non-root app
(i.e. with a context path), flurfunk-server is expected at the URL
_http://localhost:8080/flurfunk-server_. You can overwrite this by
setting the system property _flurfunk.server_.

### Creating a standalone JAR that includes Jetty ###

    lein uberjar
	mv target/flurfunk-web-*-standalone.jar target/flurfunk-web.jar

### Running the standalone JAR ###

    java -jar target/flurfunk-web.jar

This will try to connect to a server running at
http://localhost:4000. If your server is running somewhere else, you
need to set the _flurfunk.server_ system property:

    java -jar target/flurfunk-web.jar -Dflurfunk.server=http://localhost:1337

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
