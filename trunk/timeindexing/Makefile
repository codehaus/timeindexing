JARGS=

all: io time index event basic data util plugin log appl

io:
	jikes $(JARGS) com/timeindexing/io/*.java

time:
	jikes $(JARGS) com/timeindexing/time/*.java

index:
	jikes $(JARGS) com/timeindexing/index/*.java

event:
	jikes $(JARGS) com/timeindexing/event/*.java

basic:
	jikes $(JARGS) com/timeindexing/basic/*.java

data:
	jikes $(JARGS) com/timeindexing/data/*.java

util:
	jikes $(JARGS) com/timeindexing/util/*.java

plugin:
	jikes $(JARGS) com/timeindexing/plugin/*.java

log:
	jikes $(JARGS) com/timeindexing/log/*.java

appl:
	jikes $(JARGS) com/timeindexing/appl/*.java

lib: all
	jar -cf timeindex-`date '+%Y%m%d'`.jar `find com/timeindexing/ -type f -name '*.class'`

doc:
	javadoc -d ti-doc/  `find com/timeindexing/ -type d | sed -e 's%/$%%' -e 's%/%.%g'`



