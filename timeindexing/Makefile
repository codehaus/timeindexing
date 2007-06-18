JARGS=

all: io time index cache event basic data util plugin appl servlet

io:
	javac $(JARGS) com/timeindexing/io/*.java

time:
	javac $(JARGS) com/timeindexing/time/*.java

index:
	javac $(JARGS) com/timeindexing/index/*.java

cache:
	javac $(JARGS) com/timeindexing/cache/*.java

event:
	javac $(JARGS) com/timeindexing/event/*.java

basic:
	javac $(JARGS) com/timeindexing/basic/*.java

data:
	javac $(JARGS) com/timeindexing/data/*.java

util:
	javac $(JARGS) com/timeindexing/util/*.java

plugin:
	javac $(JARGS) com/timeindexing/plugin/*.java

appl:
	javac $(JARGS) com/timeindexing/appl/*.java
	javac $(JARGS) com/timeindexing/appl/playlist/*.java

servlet:
	javac $(JARGS) com/timeindexing/servlet/*.java


lib: all
	jar -cf timeindex-`date '+%Y%m%d'`.jar `find com/timeindexing/ -type f -name '*.class'`

doc:
	javadoc -d ti-doc/ -classpath . `find com/timeindexing/ -type d | sed -e 's%/$$%%' -e 's%/%.%g' -e '/.CVS$$/d' -e '/com.timeindexing$$/d' `



