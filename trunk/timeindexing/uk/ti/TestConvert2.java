package uk.ti;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.AbsoluteInterval;
import com.timeindexing.basic.EndPointInterval;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.Overlap;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.DataType;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.index.IndexCreateException;
import com.timeindexing.time.Lifetime;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.time.ElapsedMillisecondTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.StringItem;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.nio.ByteBuffer;


import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * First test of TimeIndexFactory and IncoreIndex.
 */
public class TestConvert2 {
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
	IndexView index = null;

	Properties properties = new Properties();
	Properties createProperties = new Properties();

	if (args.length == 1) {
	    // have tifile name,
	    properties.setProperty("indexpath", args[0]);
	} else {
	    System.err.println("usage: TestConvert2 <indexfile>");
	    System.exit(1);
	}

	try {
	    index = factory.open(properties);

	    IndexView narrow1 = index.select(new EndPointInterval(new AbsolutePosition(10), new AbsolutePosition(40)),
					     IndexTimestampSelector.DATA,
					     Overlap.FREE,
					     Lifetime.CONTINUOUS);

	    System.err.println("converting........");

	    Properties convertProperties = new Properties();
	    convertProperties.setProperty("name", "testconvert2");
	    convertProperties.setProperty("indexpath", "/tmp/testconvert2");

	    Index convIndex = factory.save(narrow1, IndexType.EXTERNAL, convertProperties);

	    convIndex.close();

	    printIndex(convIndex);

	    factory.close(index);

	} catch (TimeIndexException ice) {
	    System.err.println("TestConvert2: " + ice.getMessage());
	    System.exit(1);
	}
    }

    public static void printIndex(Index index) throws TimeIndexException {
	System.out.print("Name: " + index.getName() + "\n");
	System.out.print("URI: " + index.getURI() + "\n");
	System.out.print("Start:\t" + index.getStartTime() + "\n");
	System.out.print("First:\t" + index.getFirstTime() + "\n");
	System.out.print("Last:\t" + index.getLastTime() + "\n");
	System.out.print("End:\t" + index.getEndTime() + "\n");

	System.out.println("Length: " + index.getLength() + " items\n\n");

	long total = index.getLength();
	for (long i=0; i<total; i++) {
	    IndexItem itemN = index.getItem(i);
	    printIndexItem(itemN);
	}
	    
	    
    }

    public static void printIndexItem(IndexItem item) throws TimeIndexException {

	System.out.print(item.getDataTimestamp() + "\t");

	System.out.print(item.getIndexTimestamp() + "\t");

	ByteBuffer itemdata = item.getData();
	if (item.getDataSize().value() > 16) {
	    System.out.print(new String(itemdata.array()).substring(0,11) + "....\t");
	} else {
	    System.out.print(new String(itemdata.array()) + "\t");
	}

	System.out.print(item.getDataSize() + "\t");

	System.out.print(item.getItemID() + "\t");

	System.out.print(item.getAnnotationMetaData() + "\n");

    }

    public static void delay(long ms) {
	try {
	    Thread.sleep(ms);
	} catch (java.lang.InterruptedException ie) {
	    ;
	}
    }
}

