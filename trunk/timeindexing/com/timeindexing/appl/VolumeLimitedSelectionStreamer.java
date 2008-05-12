// VolumeLimitedSelectionStreamer.java

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
 * the amount to a certain volume of 
 */
public class VolumeLimitedSelectionStreamer extends SelectionStreamer  {
    /**
     * Construct a VolumeLimitedSelectionStreamer object given
     * an index and an output stream.
     */
    public VolumeLimitedSelectionStreamer(Index anIndex, OutputStream output) {
	super(anIndex, output);
    }

    /**
     * Process the TimeIndex
     */
    public long processTimeIndex(IndexView selection) throws IOException, TimeIndexException {
	// output the selection
	long writeCount = 0;
	long length = selection.getLength();
	Count volumeLimit = null;

	// if there's any items in the selection
        if (length > 0) {
	    // get the count limit for selection from the properties
	    //SelectionProcessor processor = new SelectionProcessor();
	    System.err.println("IndexProperties = " + outputProperties);
	    String volumeLimitStr = (String)outputProperties.get("volumelimit");

	    // if a count limit was specified
	    if (volumeLimitStr != null) {
		// parse the volune
		volumeLimit = new CountParser().parse(volumeLimitStr);

		// if the count was unparsabe or was zero
		if (volumeLimit.value() == 0) {
		    // set volumeLimit to null
		    volumeLimit = null;
		}

		System.err.println("VolumeLimit = " + volumeLimit);
	    } else {
		System.err.println("No VolumeLimit specified");
	    }

	    // output the items
	    for (long i=0; i<length; i++) {
		IndexItem itemN = selection.getItem(i);

		// if there is a volumeLimit then process the relvant data
		if (volumeLimit != null) {
		    System.err.println("Elapsed volume = " + writeCount);

		    if (writeCount > volumeLimit.value()) {
			break;
		    } 
		}

		writeCount += outputPlugin.write(itemN, outputProperties);
	    }
	}
	
	return writeCount;
    }

}
