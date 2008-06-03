Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * Test of multiple TimeIndex threads to the same underlying index.
 * Uses the SelecterStreamer object to do output
 */
public class Thread2 implements Runnable {
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
		new Thread2(indexpath);

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
	System.err.println("Thread2 threadcount delay indexpath");
    }

    public Thread2(String indexpath) {
	openProperties = new Properties();
	openProperties.setProperty("indexpath", indexpath);

	myThread = new Thread(this);

	System.err.println("Thread2: Object " + this.hashCode() + ". Thread " + myThread.getName());

	myThread.start();
    }

    public void run() {
	factory = new TimeIndexFactory();

	try {
	    Timestamp startT = new MillisecondTimestamp();

	    IndexView index = factory.open(openProperties);

	    RelativeTimestamp elapsed = TimeCalculator.elapsedSince(startT);

	    System.err.println("Thread2: Object " + this.hashCode() + ". Thread " + myThread.getName() + ". Index " + index.getID() + " opened after " + elapsed);
	    printIndex(index);

	    factory.close(index);

	} catch (TimeIndexException ice) {
	    ice.printStackTrace();
	    System.exit(1);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    System.exit(1);
	}

    }

    public void printIndex(Index index) throws TimeIndexException, IOException {
	System.err.print(myThread.getName() + "Name: " + index.getName() + "\n");
	System.err.print(myThread.getName() + "Start:\t" + index.getStartTime() + "\n");
	System.err.print(myThread.getName() + "First:\t" + index.getFirstTime() + "\n");
	System.err.print(myThread.getName() + "Last:\t" + index.getLastTime() + "\n");
	System.err.print(myThread.getName() + "End:\t" + index.getEndTime() + "\n");

	System.err.println(myThread.getName() + "Length: " + index.getLength() + " items\n\n");

	SelectionStreamer streamer = new SelectionStreamer(index, System.out);

	long totalOut = streamer.doOutput(new IndexProperties());
    }

}

