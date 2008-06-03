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

import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.time.Clock;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.AbsoluteTimestamp;
import com.timeindexing.time.NanosecondTimestamp;
import com.timeindexing.time.RelativeTimestamp;
import com.timeindexing.time.ElapsedNanosecondTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Lifetime;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.AbsoluteInterval;
import com.timeindexing.basic.EndPointInterval;
import com.timeindexing.basic.MidPointInterval;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.AbsoluteAdjustablePosition;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.RelativeCount;
import com.timeindexing.basic.Overlap;
import com.timeindexing.basic.Count;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Date;
import java.util.TimeZone;
import java.util.Random;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * First test of index selection.
 */
public class TISelect extends TIAbstractSelect {
    public static void main(String [] args) {
	Properties properties = new Properties();
	TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

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
 

	try {
	    if (args.length == 1) {
		// have tifile name,
		properties.setProperty("indexpath", args[0]);
		new TISelect(properties);
	    } else if (args.length == 0) {
		help(System.err);
	    } else {
		processArgs(args, properties);
		new TISelect(properties);
	    }
	} catch (TimeIndexException tie) {
	    System.err.println("Cannot open index \"" + args[0] + "\"");
	}
    }

    public static void help(PrintStream out) {
	out.println("tiselect [-sp pos|-st time] [-ep pos|-et time|-c count|-f elapsedtime] [-n] <tifile>");
    }


    /**
     * Build a TISelect object with a timeindex filename only
     */
    public TISelect(Properties properties)  throws TimeIndexException {
	super(properties);
    }

    /**
     * Build a TISelect object with a timeindex filename only
     */
    public TISelect(Properties properties, OutputStream output)  throws TimeIndexException {
	super(properties, output);
    }

    /**
     * Do something with the selection.
     */
    public void processSelection(IndexView selection, OutputStream out) throws TimeIndexException  {
	// output the selection
	long total = selection.getLength();

	for (long i=0; i<total; i++) {
	    IndexItem itemN = selection.getItem(i);
	    printIndexItem(itemN, out);
	}
    }

    /**
     * Print an individual IndexItem to the OutputStream.
     */
    protected void printIndexItem(IndexItem item, OutputStream out) {
	ByteBuffer itemdata = item.getData();
	byte [] outbuf = new byte[1024];
	boolean newline = true;

	// check args for newline
	if (argProps.containsKey("newline")) {
	    String nlValue = (String)argProps.get("newline");

	    if (nlValue == null) {
		newline = false;
	    } else if (nlValue.equals("true") || nlValue.equals("1")) {
		newline = true;
	    } else {
		newline = false;
	    }
	} else {
	    newline = false;
	}

	// pump out the data
	try {
	    //out.write(itemdata.array());
	    while (itemdata.remaining() >= 1024) {
		itemdata.get(outbuf, 0, 1024);
		out.write(outbuf);
	    }
	    int remaining = itemdata.remaining();
	    itemdata.get(outbuf, 0, remaining);
	    out.write(outbuf, 0, remaining);

	    if (newline) out.write('\n');
	} catch (java.io.IOException ioe) {
	    ;
	}
    
    }


	
	
}
