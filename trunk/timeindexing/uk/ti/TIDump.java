package uk.ti;


import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.TimeIndexException;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;


/**
 * Dump a timeindex file to stdout.
 */
public class TIDump extends TIAbstractRestore {
    /*
     * How many bytes of the data to dump
     */
    int dataView = 32;

    public static void main(String [] args) {
	try {
	    if (args.length == 1) {
		// have tifile name only,
		new TIDump(args[0]);
	    } else if (args.length > 1) {
		//for (int argc=0; argc < args.length; argc++) {
		help(System.err);
	    } else {
		help(System.err);
	    }

	} catch (TimeIndexException tie) {
	    System.err.println("Cannot open index \"" + args[0] + "\"");
	}

    }

    public static void help(PrintStream out) {
	out.println("tidump <tifile>");
    }

    /**
     * Build a TIDump object with a timeindex filename only
     */
    public TIDump(String tiFileName) throws TimeIndexException {
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
    protected void printIndex(Index index, OutputStream out) {
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
	ManagedFileIndexItem itemM = (ManagedFileIndexItem)item;

	StringBuffer buf = new StringBuffer(256);

	buf.append(item.getDataTimestamp() + "\t");

	buf.append(item.getIndexTimestamp() + "\t");

	buf.append(item.getDataSize() + "\t");


	ByteBuffer itemdata = item.getData();
	
	String rawData = null;	
	String outData = null;
	byte[] array = new byte[32];

	if (item.getDataSize().value() > dataView) {
	    itemdata.get(array, 0, 27);
	    rawData = new String(array, 0, 27);
	    //rawData = new String(itemdata.array()).substring(0, dataView-5);
	    outData = rawData.replace('\n', (char)182);
	    buf.append(outData + "....\t");
	} else {
	    itemdata.get(array, 0, (int)item.getDataSize().value());
	    rawData =  new String(array, 0, (int)item.getDataSize().value());
	    //rawData = new String(itemdata.array());
	    outData = rawData.replace('\n', (char)182);
	    buf.append(outData + "\t");
	}

	buf.append(itemM.getPosition() + "\t");

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
