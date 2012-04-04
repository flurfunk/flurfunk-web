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
    
Then go to http://localhost:3000/index-dev.html.

### Production mode ###

    lein cljsbuild once
    lein ring server

### Creating a WAR ###

    mkdir temp
    lein ring uberwar temp/flurfunk-web.war

### Deploying the WAR to Nexus ###

After creating a WAR, you can deploy it to Nexus like this:

    lein pom temp/pom.xml

    mvn deploy:deploy-file -Durl=https://server/nexus/content/repositories/snapshots/ \
                       -DrepositoryId=viaboxx-snapshots \
                       -Dfile=flurfunk-web.war \
                       -DpomFile=temp/pom.xml \
                       -Dpackaging=war 

### Downloading WAR from Nexus ###

After deploying to Nexus, you might want to download the WAR to a server where
it can be deployed into a container:

    wget -O flurfunk-web.war --user=jenkins-artifacts --password=PASSWORD \
        'https://server/nexus/service/local/artifact/maven/redirect?r=snapshots&g=de.viaboxx.flurfunk&a=flurfunk-web&v=0.1.0-SNAPSHOT&e=war'

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
