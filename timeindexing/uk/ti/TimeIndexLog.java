package uk.ti;

import java.util.Properties;
import com.timeindexing.index.*;
import com.timeindexing.data.*;
import com.timeindexing.time.*;

/**
 * Utility support for creating logs using timeindexes.
 */
public class TimeIndexLog {
    // a shared TimeIndexFactory for this class
    static  TimeIndexFactory factory = new TimeIndexFactory();


    /**
     * Create a log file, given a path and a name.
     */
    public static TimeIndex createIndex(String path, String name) throws TimeIndexException {
	Properties indexProperties = new Properties();
	TimeIndex index = null;

	boolean hasSlash = path.endsWith("/");
	indexProperties.setProperty("indexpath", path + 
				    (hasSlash?"":"/") + name);
	indexProperties.setProperty("name", "searchsite-" + name);

	
	index = (TimeIndex)factory.create(IndexType.INLINE_DT, indexProperties);
	factory.close(index);

	return index; 
    }

    /**
     * Open a log file, given a path and a name.
     */
    public static TimeIndex openIndex(String path, String name) throws TimeIndexException {
	Properties indexProperties = new Properties();
	TimeIndex index = null;

	boolean hasSlash = path.endsWith("/");
	indexProperties.setProperty("indexpath", path + 
				    (hasSlash?"":"/") + name);

	index = (TimeIndex)factory.open(indexProperties);
	index.activate();

	return index;
    }

    /**
     * Close a log file, given an Index
     */
    public static boolean closeIndex(Index index) throws TimeIndexException {
	return factory.close(index);
    }

    /**
     * Add an entry to a log, given a Index and the text for the entry.
     */
    public static Index addEntry(Index index, String entry) throws TimeIndexException {
	// A chunk of data
	DataItem data  = new StringItem(entry);

	index.addItem(data);
	    
	return index;
   }

    /**
     * Add an entry to a log, given an Index, a data Timestamp, and the text for the entry.
     */
    public static Index addEntry(Index index, Timestamp dataTS, String entry) throws TimeIndexException {
	// A chunk of data
	DataItem data  = new StringItem(entry);

	index.addItem(data, dataTS);
	    
	return index;
   }

}



