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
import com.timeindexing.appl.SelectionValidationCode;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.PrintStream;
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
 * First test of index selection.
 */
public class TISelectCode extends TIAbstractSelect {
    long secCode = 0;

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
		new TISelectCode(properties);
	    } else if (args.length == 0) {
		help(System.err);
	    } else {
		processArgs(args, properties);
		TISelectCode coder = new TISelectCode(properties);

		System.out.println(coder.getCode());
	    }
	} catch (TimeIndexException tie) {
	    System.err.println("Cannot open index \"" + args[0] + "\"");
	}

    }

    public static void help(PrintStream out) {
	out.println("tiselectcode [-sp pos|-st time] [-ep pos|-et time|-c count|-f elapsedtime] [-n] <tifile>");
    }


    /**
     * Build a TISelectCode object with a timeindex filename only
     */
    public TISelectCode(Properties properties)  throws TimeIndexException {
	super(properties);

    }

    /**
     * Build a TISelectCode object with a timeindex filename only
     */
    public TISelectCode(Properties properties, OutputStream output)  throws TimeIndexException {
	super(properties, output);
    }

    /**
     * Do something with the selection.
     */
    public void processSelection(IndexView selection, OutputStream out) {
	setCode(evaluateSecurityCode(selection));

	//PrintStream pout = new PrintStream(out);
	//pout.println(getCode());
    }

    /**
     * Evaluate the security code.
     */
    protected long evaluateSecurityCode(IndexView selection) {
	SelectionValidationCode generator = new SelectionValidationCode();

	return generator.generate(selection);
    }

    /**
     * Get the code 
     */
    public long getCode() {
	return ((Long)argProps.get("code")).longValue();
    }

    /**
     * Set the code 
     */
    public void setCode(long v) {
	secCode = v;
	argProps.put("code", new Long(v));
	//System.err.println("Set secCode to " + getCode());
    }
}
