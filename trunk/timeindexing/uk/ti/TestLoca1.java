package uk.ti;

import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.IndexOpenException;
import com.timeindexing.time.Clock;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.ElapsedSecondTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Lifetime;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.AbsoluteInterval;
import com.timeindexing.basic.EndPointInterval;
import com.timeindexing.basic.MidPointInterval;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.AbsoluteAdjustablePosition;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.RelativeCount;
import com.timeindexing.basic.Overlap;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Random;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * First test of index selection.
 */
public class TestLoca1 {
    int intervalStart = 0;

    int intervalEnd = 0;

    public static void main(String [] args) {
	/*
	 * Handle broken pipes properly
	 */
	final Signal sigpipe = new Signal("PIPE");

	SignalHandler handler = new SignalHandler () {
		public void handle(Signal sig) {
		    System.exit(sigpipe.hashCode());
		}
	    };
	Signal.handle(sigpipe, handler);
 


	TimeIndexFactory factory = new TimeIndexFactory();

	Properties properties = new Properties();

	if (args.length == 1) {
	    // have tifile name,
	    properties.setProperty("indexpath", args[0]);
	} else if (args.length == 0) {
	    // use default from Test5
	    properties.setProperty("indexpath", "/tmp/test5");
	} else {
	    ;
	}

	try {
	    //IndexView index = factory.open(new File(properties.getProperty("indexpath")));
	    IndexView index = null;

	    try {
		index = factory.open(properties);
	    } catch (IndexOpenException ioe) {
		System.err.println("Couldn't open \"/tmp/test5\".  You need to run Test5 first");
		System.exit(0);
	    }


	    Random rseq = new Random(System.currentTimeMillis());
	    // range 0 to indexLength-1
	    int rand = rseq.nextInt((int)index.getLength()-1);

	    System.err.println("Random position = " + rand);

	    IndexItem randomItem = null;

	    randomItem = index.getItem((long)rand);
	    //randomItem = index.getItem(index.getLength()-20);
	    //randomItem = index.getItem(index.getLength() / 2);
	

	    Timestamp ts = randomItem.getDataTimestamp();
	    // add 5 minutes to the timestamp
	    Timestamp elapsed = new ElapsedSecondTimestamp(5 * 60);
	    Timestamp newTS = ts; //TimeCalculator.addTimestamp(ts, elapsed);

	    System.err.println("Timestamp = " + ts );

	    
	    //System.err.println(" elapsed = " + elapsed + " asMicros = " + TimeCalculator.toMicros((ElapsedSecondTimestamp)elapsed) + " newTS = " + newTS);

	    TimestampMapping foundM = index.locate(newTS, IndexTimestampSelector.DATA, Lifetime.DISCRETE);

	    if (foundM == null) {
		System.err.println("Position not in index");
		System.exit(0);
	    }

	    System.err.println(" position = " + foundM);

	    if (foundM.position() == Position.TOO_LOW || foundM.position() == Position.TOO_HIGH) {
		System.err.println("Found Position out of index");
		System.exit(0);
	    }

	    Position foundP = foundM.position();

	    Interval interval1 = new MidPointInterval(new AbsolutePosition(foundP),
						      new RelativeCount(-4),
						      new RelativeCount(+4));
	    Index narrow1 = index.select(interval1, IndexTimestampSelector.DATA, Overlap.FREE, Lifetime.DISCRETE);


	    if (narrow1 == null) {
		System.err.println("Didn't do selection narrow 1 properly");
	    } else {
		System.err.println("Interval1 = " + interval1);
		printIndex(narrow1);
	    }

	    Interval interval2 = new EndPointInterval(new AbsolutePosition(foundP),
						      (Position)new AbsoluteAdjustablePosition(foundP).adjust(9));
	    Index narrow2 = index.select(interval2, IndexTimestampSelector.DATA, Overlap.FREE, Lifetime.DISCRETE);


	    if (narrow2 == null) {
		System.err.println("Didn't do selection narrow 2 properly");
	    } else {
		System.err.println("Interval2 = " + interval2);
		printIndex(narrow2);
	    }

	    // add 1 hour
	    Interval interval3 = new EndPointInterval(new AbsolutePosition(foundP),
						      new ElapsedSecondTimestamp(1 * 60 * 60));
	    Index narrow3 = index.select(interval3, IndexTimestampSelector.DATA, Overlap.FREE, Lifetime.DISCRETE);

	    if (narrow3 == null) {
		System.err.println("Didn't do selection narrow 3 properly");
	    } else {
		System.err.println("Interval3 = " + interval3);
		printIndex(narrow3);
	    }

	    // foundP , - 1hour, + 1 hour
	    Interval interval4 = new MidPointInterval(new AbsolutePosition(foundP),
						      new ElapsedSecondTimestamp(-1 * 60 * 60),
						      new ElapsedSecondTimestamp(1 * 60 * 60));
	    Index narrow4 = index.select(interval4, IndexTimestampSelector.DATA, Overlap.FREE, Lifetime.DISCRETE);


	    if (narrow4 == null) {
		System.err.println("Didn't do selection narrow 4 properly");
	    } else {
		System.err.println("Interval4 = " + interval4);
		printIndex(narrow4);
	    }


	    factory.close(index);
	} catch (TimeIndexException ioe) {
	    System.err.println("Error with index \"" + properties.getProperty("indexpath") + "\"");
	    System.exit(1);
	}	    
    }

    public static void printIndex(Index index) throws TimeIndexException  {
	System.out.print("Name: " + index.getName() + "\n");
	System.out.print("Start: " + index.getStartTime() + " ");
	System.out.print(" End: " + index.getEndTime() + " ");

	System.out.print("Length: " + index.getLength() + " items, ");
	System.out.println();

	System.out.print("First: " + index.getFirstTime() + " ");
	System.out.print("Last: " + index.getLastTime() + " ");

	System.out.print("ID:" + index.getID());
	System.out.println();
	System.out.println();

	long total = index.getLength();
	for (long i=0; i<total; i++) {
	    // get item 
	    IndexItem itemN = index.getItem(i);

	    // print it
	    printIndexItem(itemN);
	}
	    
	    
    }

    public static void printIndexItem(IndexItem item) throws TimeIndexException  {
	StringBuffer out = new StringBuffer(256);

	out.append(item.getDataTimestamp() + "\t");

	out.append(item.getIndexTimestamp() + "\t");

	ByteBuffer itemdata = item.getData();
	
	String rawData = null;	
	String outData = null;

	if (item.getDataSize().value() > 32) {
	    rawData = new String(itemdata.array()).substring(0,27);
	    outData = rawData.replace('\n', (char)182);
	    out.append(outData + "....\t");
	} else {
	    rawData = new String(itemdata.array());
	    outData = rawData.replace('\n', (char)182);
	    out.append(outData + "\t");
	}

	out.append(item.getDataSize() + "\t");

	out.append(item.getItemID() + "\t");

	out.append(item.getAnnotations() + "\t");

	ManagedFileIndexItem itemM = (ManagedFileIndexItem)item;

	out.append(itemM.getPosition() + "\t");

	out.append(itemM.getIndexOffset() + "\t");

	out.append(itemM.getDataOffset() + "\t");

	out.append("\n");

	System.out.print(out.toString());
    }

}

