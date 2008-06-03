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
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.plugin.OutputPlugin;
import com.timeindexing.appl.SelectionStreamer;
import com.timeindexing.appl.TimeLimitedSelectionStreamer;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;
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
 * First test of time limited index selection .
 */
public class TimeLimitedBetterSelect extends BetterSelect  {
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

	TimeLimitedBetterSelect selector = null;
 
	try {

	    if (args.length == 1) {
		// have tifile name,
		properties.setProperty("indexpath", args[0]);
		selector = new TimeLimitedBetterSelect(properties);
	    } else if (args.length == 0) {
		help(System.err);
	    } else {
		processArgs(args, properties);
		properties.setProperty("timelimit", "0:15");
		selector = new TimeLimitedBetterSelect(properties);
	    }

 	} catch (TimeIndexException tie) {
	    System.err.println("Cannot open index \"" + args[args.length-1] + "\"");
	}
   }

    /**
     * Build aTimeLimited BetterSelect object with a timeindex filename only
     */
    public TimeLimitedBetterSelect(Properties properties) throws TimeIndexException  {
	super(properties);
    }

    /**
     * Build a TimeLimitedBetterSelect object with a timeindex filename only
     */
    public TimeLimitedBetterSelect(Properties properties, OutputStream output) throws TimeIndexException  {
	super(properties, output);
    }

    /**
     * Print an index to the OutputStream.
     */
    protected void printIndex(Index indexIn, OutputStream out) throws TimeIndexException  {
	SelectionStreamer outputter = new TimeLimitedSelectionStreamer(indexIn, out);
	long total = 0;

	try {
	    total = outputter.doOutput(argProps);

	    //System.err.println("Wrote " + total + " bytes.");
	} catch (IOException ioe) {
	    System.err.println("OutputStreamer failed: " + ioe);
	}
    }
}

