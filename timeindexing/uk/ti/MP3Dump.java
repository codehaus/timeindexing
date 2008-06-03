package uk.ti;


import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.IndexReferenceDataHolder;
import com.timeindexing.index.TimeIndexException;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;


import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Dump a timeindex file to stdout.
 */
public class MP3Dump extends TIAbstractRestore {
    /*
     * How many bytes of the data to dump
     */
    int dataView = 32;

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
 


	try {
	    if (args.length == 1) {
		// have tifile name only,
		new MP3Dump(args[0]);
	    } else if (args.length > 1) {
		//for (int argc=0; argc < args.length; argc++) {
		help(System.err);
	    } else {
		help(System.err);
	    }

	} catch (TimeIndexException tie) {
	    System.err.println("Error for \"" + args[0] + "\" because " + tie);
	}

    }

    public static void help(PrintStream out) {
	out.println("tidump <tifile>");
    }

    /**
     * Build a MP3Dump object with a timeindex filename only
     */
    public MP3Dump(String tiFileName) throws TimeIndexException {
	init();
	dump(tiFileName, System.out);
    }

    /**
     * Dump the output.
     */
    public boolean dump(String filename, OutputStream output) throws TimeIndexException {
	return doit(filename, output);
    }

    /**
     * Print an index to the OutputStream.
     */
    protected void printIndex(Index index, OutputStream out) throws TimeIndexException {
	long total = index.getLength();
	for (long i=0; i<total; i++) {
	    IndexItem itemN = index.getItem(i);
	    printIndexItem(itemN, out);
	}
	    
	    
    }


    /**
     * Print an individual IndexItem to the OutputStream.
     */
    protected void printIndexItem(IndexItem item, OutputStream out) {
	StringBuffer buf = new StringBuffer(256);

	buf.append(item.getDataTimestamp() + "\t");

	buf.append(item.getIndexTimestamp() + "\t");

	buf.append(item.getDataSize() + "\t");

	if (item.isReference()) {
	    IndexReferenceDataHolder ref = (IndexReferenceDataHolder)((ManagedFileIndexItem)item).getDataAbstraction();
	    buf.append(ref.getIndexURI() + " => ");
	    
	    buf.append(ref.getIndexItemPosition()  + "\t");
	} else {
	    ByteBuffer itemdata = item.getData();
	

	    String rawData = null;	
	    String outData = null;
	    byte[] array = new byte[32];
	    int count = 0;

	    if (item.getDataSize().value() > dataView) {
		itemdata.get(array, 0, 10);
		count = 10;

	    } else {
		//itemdata.get(array, 0, (int)item.getDataSize().value());
		//count = (int)item.getDataSize().value();

		itemdata.get(array, 0, 10);
		count = 10;
	    }

	    for (int b=0; b < count; b++) {
		int value = 0x000000FF & array[b];

		if (value < 16) {
		    buf.append("0");
		}
		buf.append(Integer.toHexString(value));
		buf.append(" ");
	    }
	}

	buf.append(item.getDataType() + "\t");

	buf.append(item.getPosition() + "\t");

	buf.append(item.getAnnotationMetaData() + "\t");

	ManagedFileIndexItem itemM = (ManagedFileIndexItem)item;

	buf.append(itemM.getIndexOffset() + "\t");

	buf.append(itemM.getDataOffset() + "\t");

	buf.append("\n");

	try {
	    out.write(buf.toString().getBytes());
	} catch (java.io.IOException ioe) {
	    ;
	}

    }
}
