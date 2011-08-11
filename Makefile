all: flurfunk.js
dev: flurfunk-dev.js

install: flurfunk.js
	cp flurfunk.js flurfunk.css ../flurfunk-server/src/main/webapp

flurfunk.js: src/flurfunk/core.cljs
	cljsc src/flurfunk/core.cljs '{:output-to "flurfunk-dev.js" :optimizations :advanced}'

flurfunk-dev.js: src/flurfunk/core.cljs
	cljsc src/flurfunk/core.cljs '{:output-dir "out-dev" :output-to "flurfunk-dev.js" :pretty-print true}'

clean:
	rm -f flurfunk.js
	rm -f flurfunk-dev.js
	rm -rf out
	rm -rf out-dev