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

/**
 * First test of IndexReferences.
 */
public class TestRef2 {
    public static void main(String [] args) {
	GregorianCalendar calendar = new GregorianCalendar();

	Properties createProperties = new Properties();

	TimeIndexFactory factory = new TimeIndexFactory();

	try {
	    createProperties.clear();
	    createProperties.setProperty("name", "index-ref2");
	    createProperties.setProperty("indexpath", "/tmp/testref2");

	    IndexView index = factory.create(IndexType.INLINE, createProperties);

	    /* Item 0 */

	    // ZERO timestamp
	    Timestamp dTS = new ElapsedMillisecondTimestamp();
	    // now
	    Timestamp rTS = null;

	    // A chunk of data
	    DataItem data = null;


	    /* Item 0 */
	    data = new StringItem("quite a lot of stuff");

	    rTS = new MillisecondTimestamp();

	    index.addItem(data, dTS);

	    delay(100);

	    /* Item 1 */

	    // work out elasped time

	    data = new StringItem("on item 1");

	    // work out elasped time
	    dTS = TimeCalculator.elapsedSince(rTS);

	    index.addItem(data, dTS);

	    delay(100);

	    /* Item 2 */

	    data = new StringItem("this is the voice of the mysterons");

	    // work out elasped time
	    dTS = TimeCalculator.elapsedSince(rTS);

	    index.addItem(data, dTS);

	    /* A few more */
	    for (int few = 0; few < 10; few++) {

		int myDelay = 100 + (few * 10);

		data = new StringItem("delay was " + myDelay);

		delay(myDelay);

		// work out elasped time
		dTS = TimeCalculator.elapsedSince(rTS);

		index.addItem(data, dTS);

	    }

	    index.commit();

	    printIndex(index);

	    System.out.println();


	    delay(500);

	    // now create another index with referecnes to the first index.
	    createProperties.clear();
	    createProperties.setProperty("name", "index-Referrer");

	    IndexView indexReferrer = factory.create(IndexType.INCORE, createProperties);

	    // add a reference to IndexItem 0.
	    indexReferrer.addReference(index.getItem(0), index);

	    // add a reference to IndexItem 3.
	    indexReferrer.addReference(index.getItem(3), index);

	    // add a reference to IndexItem 8.
	    indexReferrer.addReference(index.getItem(8), index);

	    printIndex(indexReferrer);

	    
	    indexReferrer.close();

	    index.close();


	} catch (TimeIndexException ice) {
	    System.err.println("TestRef2: " + ice.getMessage());
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

	System.out.print(item.getAnnotations() + "\n");

    }

    public static void delay(long ms) {
	try {
	    Thread.sleep(ms);
	} catch (java.lang.InterruptedException ie) {
	    ;
	}
    }
}

