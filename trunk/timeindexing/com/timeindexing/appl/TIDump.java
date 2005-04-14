// TIDump.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.GetItemException;
import com.timeindexing.plugin.DefaultOutputPlugin;
import com.timeindexing.plugin.DefaultWriter;
import com.timeindexing.plugin.WriterPlugin;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;


import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Dump a TimeIndex to the stdout.
 */
public class TIDump {
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

	/*
	 * Process args
	 */
	Displayer display = null;
	boolean withNLs = false;
	boolean withDataTS = false;
	boolean withIndexTS = false;

	int argc = 0;

	for (argc=0; argc<args.length; argc++) {
	    if (args[argc].startsWith("-")) {    // it's a flag
		// have NL flag
		if (args[argc].equals("-n")) {
		    withNLs = true;
		} else if (args[argc].equals("-d")) {
		    withDataTS = true;
		}  else if (args[argc].equals("-i")) {
		    withIndexTS = true;
		} else if (args[argc].equals("-h")) {
		    help(System.err);
		    System.exit(1);	
		}
	    } else {
		break;
	    }
	}

	if (argc == args.length) {
	    // we've used up all the args
	    help(System.err);
	    System.exit(1);	
	} else {
	    // the next one is an index, hopefully
	    display = new Displayer(args[args.length-1]);
	}

	
	/*
	 * Go for it
	 */
	try {

	    IndexProperties displayProps = new IndexProperties();
	    // do we want the OutputPlugin to put out a \n after each item
	    displayProps.putProperty("newline", Boolean.toString(withNLs));

	    // do we want to output the data timestamp for each item
	    displayProps.putProperty("datatimestamp", Boolean.toString(withDataTS));

	    // do we want to output the index timestamp for each item
	    displayProps.putProperty("indextimestamp", Boolean.toString(withIndexTS));

	    // show the data

	    display.display(displayProps, new DefaultOutputPlugin(new TIDumpWriterPlugin()));

	} catch (TimeIndexException tie) {
	    System.err.println("TIDump: error " + tie.getMessage());
	} catch (IOException ioe) {
	    System.err.println("TIDump: error " + ioe.getMessage());
	}
    
    }

    static void help(PrintStream out) {
	out.println("tidump [-n] [-d] [-i] <tifile>");
    }

}

class TIDumpWriterPlugin extends DefaultWriter implements WriterPlugin {
    /**
     * Output properties include:
     * <ul>
     * <li> "eolmark" -> ¶ at EOL
     * <li> "newline" -> \n at EOL
     * <li> "datatimestamp" -> prints the data timestamp
     * <li> "indextimestamp" -> prints the index timestamp
     * </ul>
     */
    public long write(IndexItem item, IndexProperties outputProperties) throws IOException {
	OutputStream out = getOutputStream();

	long writeCount = 0;

	// do we want data timestamps
	boolean doDataTS = Boolean.valueOf((String)outputProperties.get("datatimestamp")).booleanValue();

	// do we want index timestamps
	boolean doIndexTS = Boolean.valueOf((String)outputProperties.get("indextimestamp")).booleanValue();

	// maybe output data timestamp
	if (doDataTS) {
	    out.write(item.getDataTimestamp().toString().getBytes());
	    out.write('\t');
	}

	// maybe output index timestamp
	if (doIndexTS) {
	    out.write(item.getIndexTimestamp().toString().getBytes());
	    out.write('\t');
	}

	// now output the data
	writeCount += super.write(item, outputProperties);

	return writeCount;
    }
}
