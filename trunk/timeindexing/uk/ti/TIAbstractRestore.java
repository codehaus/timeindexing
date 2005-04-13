package uk.ti;

import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndexException;
import java.util.Properties;
import java.util.TimeZone;
import java.io.File;
import java.io.OutputStream;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Common code for restoring a timeindex file.
 */
public abstract class TIAbstractRestore {
    TimeIndexFactory factory = null;

    Properties properties = null;

    Index index = null;

    /**
     * Initialise.
     */
    public boolean init() {
	// set 0 timezone
	TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

	factory = new TimeIndexFactory();

	properties = new Properties();

	return true;

    }
    
    /**
     * Do the main processing.
     */
    public boolean doit(String filename, OutputStream output) throws TimeIndexException {
	// set the filename property
	properties.setProperty("indexpath", filename);
	//properties.setProperty("datapath", filename);

	// set he close property if it's not already set
	if (! properties.containsKey("close")) {
	    properties.setProperty("close", "true");
	}


	// open and load a hollow Index
	index = factory.open(properties);



	// print it out
	printIndex(index, output);
	    
	// close 
	close();
	    
	return true;

    }
   
    /**
     * Close 
     */
    public void close() throws TimeIndexException {
	// close the index
	boolean doClose = Boolean.valueOf(properties.getProperty("close")).booleanValue();
	if (doClose) {
	    //System.err.println("Closing \"" + index.getName() + "\"");
	    factory.close(index);
	}
    }
	
    /**
     * Print an index to the OutputStream.
     */
    protected abstract void printIndex(Index index, OutputStream out) throws TimeIndexException ;



}
