package uk.ti;

import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.IndexOpenException;
import com.timeindexing.time.Clock;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.Lifetime;
import com.timeindexing.basic.AbsoluteInterval;
import com.timeindexing.basic.EndPointInterval;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.Overlap;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Properties;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * First test of index selection.
 */
public class TestSel1 {
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

	Timestamp t0 = Clock.time.asMillis();

	try {
	    IndexView index = factory.open(new File(properties.getProperty("indexpath")));

	    Timestamp t1 = Clock.time.asMillis();

	    System.out.print("t0: " + t0 + " ");
	    System.out.print("t1: " + t1 + " ");
	    System.out.print("time: " + TimeCalculator.elapsedSince(t0) + " ");
	    System.out.print("Length: " + index.getLength() + " items, ");
	    System.out.println();
	
	    IndexView narrow1 = index.select(new EndPointInterval(new AbsolutePosition(10),
								  new AbsolutePosition(40)),
					     IndexTimestampSelector.DATA, Overlap.FREE,
					     Lifetime.CONTINUOUS);


	    if (narrow1 == null) {
		System.err.println("Didn't do selection properly");
	    } else {
		printIndex(narrow1);
	    }

	    IndexView narrow2 = narrow1.select(new EndPointInterval(new AbsolutePosition(10),
								    new AbsolutePosition(20)),
					       IndexTimestampSelector.DATA, Overlap.FREE,
					       Lifetime.CONTINUOUS);


	    if (narrow2 == null) {
		System.err.println("Didn't do selection properly");
	    } else {
		printIndex(narrow2);
	    }

	    factory.close(index);
	} catch (IndexOpenException ioe) {
	    System.err.println("Cannot open index \"" +  properties.getProperty("indexpath") + "\"");
	    System.exit(1);
	}	    

    }

    public static void printIndex(Index index) {
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

	    // hollow it
	    index.hollowItem(i);
	}
	    
	    
    }

    public static void printIndexItem(IndexItem item) {
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

