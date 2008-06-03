package uk.ti;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.DataType;
import com.timeindexing.index.IndexCreateException;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.time.ElapsedMillisecondTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.StopWatch;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.StringItem;

import java.util.Properties;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.nio.ByteBuffer;

/**
 * First test of ExternalIndex.
 */
public class Test5C {
    public static void main(String [] args) {
	GregorianCalendar calendar = new GregorianCalendar();
	TimeIndexFactory factory = new TimeIndexFactory();

	Properties properties = new Properties();
	properties.setProperty("name", "index-Test5-External");
	properties.setProperty("indexpath", "/tmp/test5cNF");
	properties.setProperty("datapath", "/tmp/test5cNF");

	try {
	    IndexView index = factory.create(IndexType.EXTERNAL, properties);

	    Timestamp dTS = null;
	    /* Item 0 */

	    // ZERO timestamp
	    StopWatch stopWatch = new StopWatch();

	    // A chunk of data
	    DataItem data = null;
	    long id = 0;
	    List annotations = null;
	    IndexItem item = null;


	    /* Item 0 */
	    data = new StringItem("quite a lot of stuff");

	    dTS = stopWatch.read();

	    index.addItem(data, dTS);

	    stopWatch.start();

	    delay(100);

	    /* Item 1 */

	    data = new StringItem("on item 1");

	    // work out elasped time
	    dTS = stopWatch.read();

	    index.addItem(data, dTS);


	    delay(100);

	    /* Item 2 */

	    data = new StringItem("this is the voice of the mysterons");

	    // work out elasped time
	    dTS = stopWatch.read();

	    index.addItem(data, dTS);

	    System.err.println("Turninn on auto commit.");
	    index.setAutoCommit(false);

	    /* A few more */
	    for (int few = 0; few < 10000; few++) {

		int myDelay = 100 + (int)(System.currentTimeMillis() % 100);

		delay(myDelay);

		// work out elasped time
		dTS = stopWatch.read();

		data = new StringItem("delay was " + myDelay);

		index.addItem(data, dTS);

		if (few % 100 == 0) {
		    // print a message occassionally
		    System.err.println("Item " + few + " @ " + dTS);
		}

	    }

	    delay(100);

	    factory.close(index);

	    printIndex(index);


	} catch (TimeIndexException ice) {
	    System.err.println("Test5B: " + ice.getMessage());
	    ice.printStackTrace();
	    System.exit(1);
	}
    }

    public static void printIndex(Index index) throws TimeIndexException {
	System.out.print("Name: " + index.getName() + "\t");
	System.out.print("Start: " + index.getStartTime() + " ");
	System.out.print("End: " + index.getEndTime() + " ");

	System.out.print("Length: " + index.getLength() + " items, ");

	System.out.println("ID:" + index.getID());

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

	System.out.print(item.getAnnotationMetaData() + "\t");

	ManagedFileIndexItem itemM = (ManagedFileIndexItem)item;

	System.out.print(itemM.getPosition() + "\t");

	System.out.print(itemM.getIndexOffset() + "\t");

	System.out.print(itemM.getDataOffset() + "\t");

	System.out.println();

    }

    public static void delay(long ms) {
	try {
	    Thread.sleep(ms);
	} catch (java.lang.InterruptedException ie) {
	    ;
	}
    }
}

