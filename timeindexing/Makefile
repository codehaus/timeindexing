

all: io time index event basic data util plugin log appl

io:
	jikes com/timeindexing/io/*.java

time:
	jikes com/timeindexing/time/*.java

index:
	jikes com/timeindexing/index/*.java

event:
	jikes com/timeindexing/event/*.java

basic:
	jikes com/timeindexing/basic/*.java

data:
	jikes com/timeindexing/data/*.java

util:
	jikes com/timeindexing/util/*.java

plugin:
	jikes com/timeindexing/plugin/*.java

log:
	jikes com/timeindexing/log/*.java

appl:
	jikes com/timeindexing/appl/*.java

lib: all
	jar -cf timeindex-`date '+%Y%m%d'`.jar `find com/timeindexing/ -type f -name '*.class'`

doc:
	javadoc -d ti-doc/  `find com/timeindexing/ -type d | sed -e 's;/$;;' -e 's;/;.;g'`

