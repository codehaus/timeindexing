package uk.ti;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.DataType;
import com.timeindexing.index.IndexCreateException;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.time.ElapsedMillisecondTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.StringItem;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.nio.ByteBuffer;

/**
 * Test of multiple TimeIndex threads to the same underlying index.
 */
public class Thread1 implements Runnable {
    Properties openProperties = null;
    TimeIndexFactory factory = null;
    Index index = null;

    Thread myThread = null;

    public static void main(String [] args) {
	if (args.length != 2) {
	    error();
	} else {
	    String delayStr = args[0];
	    int delay = 0;
	    String indexpath = args[1];

	    try {
		delay = Integer.parseInt(delayStr);
	    } catch (NumberFormatException nfe) {
		delay = 0;
	    }

	    new Thread1(indexpath);

	    delay(delay);

	    new Thread1(indexpath);
	    
	    delay(delay);

	    new Thread1(indexpath);
	    
	}
    }

    public static void delay(long ms) {
	try {
	    Thread.sleep(ms);
	} catch (java.lang.InterruptedException ie) {
	    ;
	}
    }

    public static void error() {
	System.err.println("Thread1 delay indexpath");
    }

    public Thread1(String indexpath) {
	openProperties = new Properties();
	openProperties.setProperty("indexpath", indexpath);

	myThread = new Thread(this);

	System.err.println("Thread1: Object " + this.hashCode() + ". Thread " + myThread.getName());

	myThread.start();
    }

    public void run() {
	factory = new TimeIndexFactory();

	try {
	    IndexView index = factory.open(openProperties);

	    System.err.println("Thread1: Object " + this.hashCode() + ". Thread " + myThread.getName() + ". Index " + index.getID() + " opened");
	    printIndex(index);

	    factory.close(index);

	} catch (TimeIndexException ice) {
	    ice.printStackTrace();
	    System.exit(1);
	}
    }

    public void printIndex(Index index) throws TimeIndexException {
	System.out.print(myThread.getName() + "Name: " + index.getName() + "\n");
	System.out.print(myThread.getName() + "Start:\t" + index.getStartTime() + "\n");
	System.out.print(myThread.getName() + "First:\t" + index.getFirstTime() + "\n");
	System.out.print(myThread.getName() + "Last:\t" + index.getLastTime() + "\n");
	System.out.print(myThread.getName() + "End:\t" + index.getEndTime() + "\n");

	System.out.println(myThread.getName() + "Length: " + index.getLength() + " items\n\n");

	long total = index.getLength();
	for (long i=0; i<total; i++) {
	    IndexItem itemN = index.getItem(i);
	    printIndexItem(itemN);
	    //delay(10);
	}
	    
	    
    }

    public void printIndexItem(IndexItem item) throws TimeIndexException {
	/*

	System.out.print(myThread.getName() + ":\t");

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
	*/
	ByteBuffer itemdata = item.getData();
	
	System.out.print(new String(itemdata.array()));
	
    }

}

