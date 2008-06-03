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
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.StoredIndex;
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
import com.timeindexing.appl.SelectionProcessor;
import com.timeindexing.cache.*;

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
public class TIVolume extends TIAbstractSelect {
    public static void main(String [] args) {
	Properties properties = new Properties();

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
		new TIVolume(properties);
	    } else if (args.length == 0) {
		help(System.err);
	    } else {
		processArgs(args, properties);
		new TIVolume(properties);
	    }
	} catch (TimeIndexException tie) {
	    System.err.println("Cannot open index \"" + args[0] + "\"");
	}
    }

    public static void help(PrintStream out) {
	out.println("tivolume [-sp pos|-st time] [-ep pos|-et time|-c count|-f elapsedtime] [-n] <tifile>");
    }


    /**
     * Build a TIVolume object with a timeindex filename only
     */
    public TIVolume(Properties properties)  throws TimeIndexException {
	super(properties);
    }

    /**
     * Build a TIVolume object with a timeindex filename only
     */
    public TIVolume(Properties properties, OutputStream output)  throws TimeIndexException {
	super(properties, output);
    }

    /**
     * Print an index to the OutputStream.
     */
    protected void printIndex(Index indexIn, OutputStream out) throws TimeIndexException  {
	indexIn.setCachePolicy(new RemoveAfterUsePolicy());
	indexIn.setLoadDataAutomatically(false);

	SelectionProcessor selector = new SelectionProcessor();
 
	IndexView selection = selector.select((TimeIndex)indexIn, argProps);

	if (selection == null) {
	     System.err.println("Didn't do selection properly");
	} else {
	    processSelection(selection, out);
	}
    }

    /**
     * Do something with the selection.
     */
    public void processSelection(IndexView selection, OutputStream out) throws TimeIndexException  {
	// output the selection
	long total = selection.getLength();
	long volume = 0;

	for (long i=0; i<total; i++) {
	    IndexItem itemN = selection.getItem(i);

	    volume += itemN.getDataSize().value();
	}


	System.out.println("Length = " + total + " Volume = " + volume);
    }

	
}
