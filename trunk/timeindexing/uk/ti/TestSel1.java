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

import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.IndexOpenException;
import com.timeindexing.time.Clock;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.Lifetime;
import com.timeindexing.time.*;
import com.timeindexing.basic.AbsoluteInterval;
import com.timeindexing.basic.EndPointInterval;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.Overlap;
import com.timeindexing.basic.RelativeCount;
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
	    properties.setProperty("indexpath", "/tmp/test5e");
	} else {
	    ;
	}

	Timestamp t0 = Clock.time.asMillis();

	try {
	    //IndexView index = factory.open(new File(properties.getProperty("indexpath")));
	    IndexView index = null;

	    try {
		index = factory.open(properties);
	    } catch (IndexOpenException ioe) {
		System.err.println("Couldn't open \"" + properties.getProperty("indexpath") + "\". ");
		System.exit(0);
	    }

	    System.out.println();
	
	    System.out.println("Narrowing. index -> narrow1");

	    IndexView narrow1 = index.select(new EndPointInterval(new AbsolutePosition(55),
								  new AbsolutePosition(95)),
					     IndexTimestampSelector.DATA, Overlap.FREE,
					     Lifetime.DISCRETE);


	    if (narrow1 == null) {
		System.err.println("Didn't do selection properly");
	    } else {
		printIndex(narrow1);
	    }

	    System.out.println("Narrowing. narrow1 -> narrow2");

	    IndexView narrow2 = narrow1.select(new EndPointInterval(new AbsolutePosition(10),
								    new Second(2, TimeDirection.FORWARD)),
					       IndexTimestampSelector.DATA, Overlap.FREE,
					       Lifetime.DISCRETE);


	    if (narrow2 == null) {
		System.err.println("Didn't do selection properly");
	    } else {
		printIndex(narrow2);
	    }

	    System.out.println("Narrowing. narrow1 -> narrow3");

	    IndexView narrow3 = narrow1.select(new EndPointInterval(new AbsolutePosition(10),
								    new AbsolutePosition(30)),
					       IndexTimestampSelector.DATA, Overlap.FREE,
					       Lifetime.DISCRETE);


	    if (narrow3 == null) {
		System.err.println("Didn't do selection properly");
	    } else {
		printIndex(narrow3);
	    }

	    System.out.println("Narrowing. narrow1 -> narrow4");

	    IndexView narrow4 = narrow1.select(new EndPointInterval(new AbsolutePosition(10),
								    new RelativeCount(10)),
					       IndexTimestampSelector.DATA, Overlap.FREE,
					       Lifetime.DISCRETE);


	    if (narrow4 == null) {
		System.err.println("Didn't do selection properly");
	    } else {
		printIndex(narrow4);
	    }

	    System.out.println("Narrowing. narrow1 -> narrow5");

	    IndexView narrow5 = narrow1.select(new EndPointInterval(new AbsolutePosition(10),
								    new ElapsedSecondTimestamp(2)),
					       IndexTimestampSelector.DATA, Overlap.FREE,
					       Lifetime.DISCRETE);


	    if (narrow5 == null) {
		System.err.println("Didn't do selection properly");
	    } else {
		printIndex(narrow5);
	    }

	    System.out.println("Narrowing. narrow1 -> narrow6");

	    IndexView narrow6 = narrow1.select(new EndPointInterval(new MillisecondTimestamp(10, 900000000),
								    new Second(2, TimeDirection.FORWARD)),
					       IndexTimestampSelector.DATA, Overlap.FREE,
					       Lifetime.DISCRETE);


	    if (narrow6 == null) {
		System.err.println("Didn't do selection properly");
	    } else {
		printIndex(narrow6);
	    }

	    System.out.println("Narrowing. narrow1 -> narrow7");

	    IndexView narrow7 = narrow1.select(new EndPointInterval(new MillisecondTimestamp(10, 900000000),
								    new AbsolutePosition(30)),
					       IndexTimestampSelector.DATA, Overlap.FREE,
					       Lifetime.DISCRETE);


	    if (narrow7 == null) {
		System.err.println("Didn't do selection properly");
	    } else {
		printIndex(narrow7);
	    }

	    System.out.println("Narrowing. narrow1 -> narrow8");

	    IndexView narrow8 = narrow1.select(new EndPointInterval(new MillisecondTimestamp(10, 900000000),
								    new RelativeCount(10)),
					       IndexTimestampSelector.DATA, Overlap.FREE,
					       Lifetime.DISCRETE);


	    if (narrow8 == null) {
		System.err.println("Didn't do selection properly");
	    } else {
		printIndex(narrow8);
	    }

	    System.out.println("Narrowing. narrow1 -> narrow9");

	    IndexView narrow9 = narrow1.select(new EndPointInterval(new MillisecondTimestamp(10, 900000000),
								    new ElapsedSecondTimestamp(2)),
					       IndexTimestampSelector.DATA, Overlap.FREE,
					       Lifetime.DISCRETE);


	    if (narrow9 == null) {
		System.err.println("Didn't do selection properly");
	    } else {
		printIndex(narrow9);
	    }

	    factory.close(index);
	} catch (TimeIndexException ioe) {
	    System.err.println("Error with index \"" +  properties.getProperty("indexpath") + "\"");
	    System.exit(1);
	}	    

    }

    public static void printIndex(IndexView index) throws TimeIndexException {
	System.out.print("Interval:" + index.getSelectionInterval() + " ");
	System.out.print("Start: " + index.getStartPosition() + " ");
	System.out.print("End: " + index.getEndPosition() + "\n");

	System.out.print("Length: " + index.getLength() + " items\n");

	System.out.print("Name: " + index.getName() + "\n");
	System.out.print("Start: " + index.getStartTime() + " ");
	System.out.print(" End: " + index.getEndTime() + " ");
	System.out.println();

	System.out.print("First: " + index.getFirstTime() + " ");
	System.out.print("Last: " + index.getLastTime() + " ");
	System.out.println();

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

	out.append(item.getDataTimestamp() + "  \t");

	out.append(item.getIndexTimestamp() + "\t");

	ByteBuffer itemdata = item.getData();
	
	String rawData = null;	
	String outData = null;

	ManagedFileIndexItem itemM = (ManagedFileIndexItem)item;

	out.append(itemM.getPosition() + "\t");

	if (item.getDataSize().value() > 26) {
	    rawData = new String(itemdata.array()).substring(0,21);
	    outData = rawData.replace('\n', (char)182);
	    out.append(outData + ".... ");
	} else {
	    rawData = new String(itemdata.array());
	    outData = rawData.replace('\n', (char)182);

	    out.append(outData);

            for (int c=rawData.length(); c <= 24; c++) {
		out.append('_');
	    }
	    out.append(" ");
	}

	out.append(item.getDataSize());

	out.append("\n");

	System.out.print(out.toString());
    }

}

