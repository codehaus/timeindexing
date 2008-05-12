package uk.ti;

import com.timeindexing.basic.ID;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.DataType;
import com.timeindexing.index.IndexCreateException;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.time.ElapsedMillisecondTimestamp;
import com.timeindexing.time.RelativeTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.StringItem;
import com.timeindexing.appl.SelectionStreamer;
import com.timeindexing.cache.*;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * Test of multiple TimeIndex threads to the same underlying index.
 * Uses the SelecterStreamer object to do output
 */
public class Thread4 implements Runnable {
    Properties openProperties = null;
    TimeIndexFactory factory = null;
    Index index = null;

    Thread myThread = null;

    public static void main(String [] args) {
	if (args.length != 3) {
	    error();
	} else {
	    String threadStr = args[0];
	    int threadCount = 0;
	    String delayStr = args[1];
	    int delay = 0;
	    String indexpath = args[2];

	    try {
		threadCount = Integer.parseInt(threadStr);
	    } catch (NumberFormatException nfe) {
		threadCount = 1;
	    }

	    try {
		delay = Integer.parseInt(delayStr);
	    } catch (NumberFormatException nfe) {
		delay = 0;
	    }

	    for (int t=0; t < threadCount; t++) {
		new Thread4(indexpath);

		delay(delay);
	    }
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
	System.err.println("Thread4 threadcount delay indexpath");
    }

    public Thread4(String indexpath) {
	openProperties = new Properties();
	openProperties.setProperty("indexpath", indexpath);

	myThread = new Thread(this);

	//System.err.println("Thread4: Object " + this.hashCode() + ". Thread " + myThread.getName());

	myThread.start();
    }

    public void run() {
	factory = new TimeIndexFactory();

	try {
	    Timestamp startT = new MillisecondTimestamp();

	    IndexView index = factory.open(openProperties);

	    RelativeTimestamp elapsed = TimeCalculator.elapsedSince(startT);

	    System.err.println("Thread4: Object " + this.hashCode() + ". Thread " + myThread.getName() + ". Index " + index.getID() + " opened after " + elapsed);

	    // get the cache
	    CachePolicy policy = index.getCachePolicy();

	    //System.err.println("CachePolicy = " + policy.getClass());

	    // allocate and start a policy viewer
	    PolicyViewer viewer = PolicyViewer.get(policy);

	    printIndex(index);

	    boolean reallyClosed = factory.close(index);

	    if (reallyClosed) {
		// last close should really close
		System.err.println("Last close");
		viewer.stop();
	    }

	} catch (TimeIndexException ice) {
	    ice.printStackTrace();
	    System.exit(1);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    System.exit(1);
	}

    }

    public void printIndex(Index index) throws TimeIndexException, IOException {
	/*
	System.err.print(myThread.getName() + "Name: " + index.getName() + "\n");
	System.err.print(myThread.getName() + "Start:\t" + index.getStartTime() + "\n");
	System.err.print(myThread.getName() + "First:\t" + index.getFirstTime() + "\n");
	System.err.print(myThread.getName() + "Last:\t" + index.getLastTime() + "\n");
	System.err.print(myThread.getName() + "End:\t" + index.getEndTime() + "\n");

	System.err.println(myThread.getName() + "Length: " + index.getLength() + " items\n\n");
	*/

	SelectionStreamer streamer = new SelectionStreamer(index, System.out);

	long totalOut = streamer.doOutput(new IndexProperties());
    }

}

class PolicyViewer implements Runnable {
    static PolicyViewer viewer = null;
    CachePolicy policy = null;
    IndexCache cache = null;
    Thread viewerThread = null;
    boolean running = false;

    public PolicyViewer(CachePolicy p) {
	policy = p;
	cache = p.getIndexCache();

	viewerThread = new Thread(this);
	viewerThread.start();

    }

    static synchronized PolicyViewer get(CachePolicy p) {
	if (viewer != null) {
	    return viewer;
	} else {
	    viewer = new PolicyViewer(p);
	    return viewer;
	}
    }
	
    public void run() {
	running = true;

	while (running) {
	    try {
		// sleep 5 seconds
		Thread.sleep(5000);

		System.err.println("Cache: size: " + cache.size() + " volume: " + cache.getDataVolume() + ". Thread " + Thread.currentThread().getName());
		System.err.println(policy);
	    } catch (InterruptedException ie) {
	    }
	}
    }

    public void stop() {
	running = false;
	viewerThread.interrupt();
    }

}