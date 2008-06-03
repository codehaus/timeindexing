/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
import java.util.Random;
import java.nio.ByteBuffer;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Test of multiple TimeIndex threads to the same underlying index.
 * Uses the SelecterStreamer object to do output
 */
public class ThreadSelect1 implements Runnable {
    Properties openProperties = null;
    TimeIndexFactory factory = null;
    Index index = null;
    int choice = 0;
    static long id = 0;

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

	    id = System.currentTimeMillis();

	    for (int t=0; t < threadCount; t++) {
		new ThreadSelect1(indexpath, t+1);

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
	System.err.println("ThreadSelect1 threadcount delay indexpath");
    }

    public ThreadSelect1(String indexpath, int choice) {
	this.choice = choice;
	openProperties = new Properties();
	openProperties.setProperty("indexpath", indexpath);

	myThread = new Thread(this);

	System.err.println("ThreadSelect1: Object " + this.hashCode() + ". Thread " + myThread.getName());

	myThread.start();
    }

    public void run() {
	factory = new TimeIndexFactory();

	try {
	    Timestamp startT = new MillisecondTimestamp();

	    IndexView index = factory.open(openProperties);
	    index.setCachePolicy(new HollowAtDataVolumePolicy(64*1024));

	    //new HollowAtDataVolumeRemoveAfterTimeoutPolicy(20*1024, new ElapsedMillisecondTimestamp(200)));
	    //new HollowAtDataVolumeRemoveAfterTimeoutPolicy());

	    RelativeTimestamp elapsed = TimeCalculator.elapsedSince(startT);

	    System.err.println("ThreadSelect1: Object " + this.hashCode() + ". Thread " + myThread.getName() + ". Index " + index.getID() + " opened after " + elapsed);
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
	OutputStream out = new FileOutputStream(new File("/tmp/tix/", "ThreadSelect1-" + choice + "-" + id));
	SelectionStreamer streamer = new SelectionStreamer(index, out);

	IndexProperties properties = new IndexProperties();

	long length = index.getLength();
	long refPoint = length/2;
	long diff = refPoint/choice;

	properties.putProperty("startpos", "" + (refPoint - diff));
	properties.putProperty("endpos", "" + (refPoint + diff));

	    
	long totalOut = streamer.doOutput(properties);
    }

}

