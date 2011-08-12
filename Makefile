SOURCES=src/flurfunk/core.cljs src/flurfunk/client.cljs

all: flurfunk.js
dev: flurfunk-dev.js

install: flurfunk.js
	cp flurfunk.js flurfunk.css ../flurfunk-server/src/main/webapp

flurfunk.js: $(SOURCES)
	cljsc src '{:output-to "flurfunk.js" :optimizations :advanced}'

flurfunk-dev.js: $(SOURCES)
	cljsc src '{:output-dir "out-dev" :output-to "flurfunk-dev.js" :pretty-print true}'

clean:
	rm -f flurfunk.js
	rm -f flurfunk-dev.js
	rm -rf out
	rm -rf out-dev