SOURCES=src/flurfunk/core.cljs \
	src/flurfunk/client.cljs \
	src/flurfunk/dom-helpers.cljs

all: flurfunk.js
dev: flurfunk-dev.js
war: flurfunk-web.war

flurfunk.js: $(SOURCES)
	rm -rf out flurfunk.js
	cljsc src '{:output-to "flurfunk.js" :optimizations :advanced}'

flurfunk-dev.js: $(SOURCES)
	rm -rf out-dev flurfunk-dev.js
	cljsc src '{:output-dir "out-dev" :output-to "flurfunk-dev.js" :pretty-print true}'

flurfunk-web.war: flurfunk.js flurfunk.css index.html
	zip flurfunk-web.war index.html flurfunk.js flurfunk.css

clean:
	rm -rf flurfunk-web.war
	rm -rf out flurfunk.js
	rm -rf out-dev flurfunk-dev.js
