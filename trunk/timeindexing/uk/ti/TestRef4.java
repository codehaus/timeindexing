package uk.ti;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.DataType;
import com.timeindexing.index.IndexReferenceDataHolder;
import com.timeindexing.index.IndexCreateException;
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
 * First test of IndexReferences.
 */
public class TestRef4 {
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
	Index index = null;

	Properties properties = new Properties();
	Properties createProperties = new Properties();

	if (args.length == 1) {
	    // have tifile name,
	    properties.setProperty("indexpath", args[0]);
	} else {
	    System.err.println("usage: TestRef4 <indexfile>");
	}

	try {
	    index = factory.open(properties);

	    // now create another index with referecnes to the first index.
	    createProperties.clear();
	    createProperties.setProperty("name", index.getName() + "-referrer");
	    createProperties.setProperty("indexpath", "/tmp/" + index.getName() + "-referrer");

	    IndexView indexReferrer = factory.create(IndexType.EXTERNAL, createProperties);


	    // add a reference every 100 items
	    long indexLength = index.getLength();
	    long SKIP = 100;

	    for (long skip = 0; skip < indexLength; skip += SKIP) {
	    // add a reference to IndexItem skip
		indexReferrer.addReference(index.getItem(skip), index);
	    }

	    printIndex(indexReferrer);

	    
	    indexReferrer.close();

	    index.close();


	} catch (TimeIndexException ice) {
	    System.err.println("TestRef4: " + ice.getMessage());
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
	if (item.isReference()) {
	    IndexReferenceDataHolder ref = (IndexReferenceDataHolder)((ManagedIndexItem)item).getDataAbstraction();
	    System.out.print(ref.getIndexURI() + "\t");
	    
	    System.out.print(ref.getIndexItemPosition()  + "   ");

	    System.out.print("=>   ");
	}

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

