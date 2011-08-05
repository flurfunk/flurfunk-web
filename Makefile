all: flurfunk.js

install: flurfunk.js
	cp flurfunk.js ../flurfunk-server/src/main/webapp

flurfunk.js: src/flurfunk/core.cljs
	cljsc src/flurfunk/core.cljs {:optimizations :advanced} > flurfunk.js

clean:
	rm -f flurfunk.js
	rm -rf out