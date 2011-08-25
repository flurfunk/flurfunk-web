SOURCES=src/flurfunk/core.cljs \
	src/flurfunk/client.cljs \
	src/flurfunk/dom-helpers.cljs

all: flurfunk.js
dev: flurfunk-dev.js

install: flurfunk.js
	cp flurfunk.js flurfunk.css ../flurfunk-server/src/main/webapp

flurfunk.js: $(SOURCES)
	rm -rf out flurfunk.js
	cljsc src '{:output-to "flurfunk.js" :optimizations :advanced}'

flurfunk-dev.js: $(SOURCES)
	rm -rf out-dev flurfunk-dev.js
	cljsc src '{:output-dir "out-dev" :output-to "flurfunk-dev.js" :pretty-print true}'

clean:
	rm -rf out flurfunk.js
	rm -rf out-dev flurfunk-dev.js
