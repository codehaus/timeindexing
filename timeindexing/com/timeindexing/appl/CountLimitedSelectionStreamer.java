// CountLimitedSelectionStreamer.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.basic.Count;
import com.timeindexing.appl.SelectionProcessor;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A class to output a selction of the data, but which limits
 * the amount to a certain number of items.
 */
public class CountLimitedSelectionStreamer extends SelectionStreamer  {
    /**
     * Construct a CountLimitedSelectionStreamer object given
     * an index and an output stream.
     */
    public CountLimitedSelectionStreamer(Index anIndex, OutputStream output) {
	super(anIndex, output);
    }

    /**
     * Process the TimeIndex
     */
    public long processTimeIndex(IndexView selection) throws IOException, TimeIndexException {
	// output the selection
	long writeCount = 0;
	long length = selection.getLength();
	Count countLimit = null;
	long count = 0;

	// if there's any items in the selection
        if (length > 0) {
	    // get the count limit for selection from the properties
	    SelectionProcessor processor = new SelectionProcessor();
	    System.err.println("IndexProperties = " + outputProperties);
	    String countLimitStr = (String)outputProperties.get("countlimit");

	    // if a count limit was specified
	    if (countLimitStr != null) {
		// parse the count
		countLimit = new CountParser().parse(countLimitStr);

		// if the count was unparsabe or was zero
		if (countLimit.value() == 0) {
		    // set countLimit to null
		    countLimit = null;
		}

		System.err.println("CountLimit = " + countLimit);
	    } else {
		System.err.println("No Count Limit specified");
	    }

	    // output the items
	    for (long i=0; i<length; i++) {
		IndexItem itemN = selection.getItem(i);

		// if there is a countLimit then process the relvant data
		if (countLimit != null) {
		    count++;
		    System.err.println("Elapsed Count = " + count);

		    if (count > countLimit.value()) {
			break;
		    } 
		}

		writeCount += outputPlugin.write(itemN);
	    }
	}
	
	return writeCount;
    }

}
