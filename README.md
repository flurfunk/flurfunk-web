Flurfunk web frontend
=====================

A client for the Flurfunk server, written in ClojureScript and built using
Leiningen.

Installing Leiningen
--------------------

    curl -O https://raw.github.com/technomancy/leiningen/stable/bin/lein
    chmod +x lein
    mv lein ~/bin/ # Make sure that ~/bin/ exists and is on the $PATH

Building and running
--------------------

### Development mode ###

    lein cljsbuild auto
    lein ring server-headless
    
Then go to http://localhost:3000/index-dev.html.

### Production mode ###

    lein cljsbuild once
    lein ring server

Creating a WAR
--------------

    mkdir temp
    lein ring uberwar temp/flurfunk-web.war

Deploying the WAR to Nexus
--------------------------

After creating a WAR, you can deploy it to Nexus like this:

    lein pom temp/pom.xml

    mvn deploy:deploy-file -Durl=https://server/nexus/content/repositories/snapshots/ \
                       -DrepositoryId=viaboxx-snapshots \
                       -Dfile=flurfunk-web.war \
                       -DpomFile=temp/pom.xml \
                       -Dpackaging=war 

Downloading WAR from Nexus
--------------------------

After deploying to Nexus, you might want to download the WAR to a server where
it can be deployed into a container:

    wget -O flurfunk-web.war --user=jenkins-artifacts --password=PASSWORD \
        'https://server/nexus/service/local/artifact/maven/redirect?r=snapshots&g=de.viaboxx.flurfunk&a=flurfunk-web&v=0.1.0-SNAPSHOT&e=war'
