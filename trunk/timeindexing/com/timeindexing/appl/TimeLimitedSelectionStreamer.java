// TimeLimitedSelectionStreamer.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.appl.SelectionProcessor;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A class to output a selction of the data, but which limits
 * the amount to a certain amount of time.
 */
public class TimeLimitedSelectionStreamer extends SelectionStreamer  {
    /**
     * Construct anTimeLimited SelectionStreamer object given
     * an index and an output stream.
     */
    public TimeLimitedSelectionStreamer(Index anIndex, OutputStream output) {
	super(anIndex, output);
    }

    /**
     * Process the TimeIndex
     */
    public long processTimeIndex(IndexView selection) throws IOException, TimeIndexException {
	// output the selection
	long writeCount = 0;
	long length = selection.getLength();
	Timestamp timeLimit = null;
	Timestamp startTime = null;
	Timestamp elapsedTime = null;

	// if there's any items in the selection
        if (length > 0) {
	    // get the time limit for selection from the properties
	    SelectionProcessor processor = new SelectionProcessor();
	    System.err.println("IndexProperties = " + outputProperties);
	    String timeLimitStr = (String)outputProperties.get("timelimit");

	    // if a time limit was specified
	    if (timeLimitStr != null) {
		// parse the time 
		timeLimit = new TimeParser().parse(timeLimitStr, false);

		// if the time was unparsabe or was zero
		if (timeLimit.getSeconds() == 0 && timeLimit.getNanoSeconds() == 0) {
		    // set timeLimit to null
		    timeLimit = null;
		}

		System.err.println("TimeLimit = " + timeLimit);
	    } else {
		System.err.println("No Time Limit specified");
	    }

	    // get the start time from the dataTS
	    startTime = selection.getItem(0).getDataTimestamp();

	    System.err.println("Start Time = " + startTime);

	    // output the items
	    for (long i=0; i<length; i++) {
		IndexItem itemN = selection.getItem(i);

		// if there is a timeLimit then process the relvant data
		if (timeLimit != null) {
		    Timestamp itemTime = itemN.getDataTimestamp();

		    elapsedTime = TimeCalculator.subtractTimestamp(itemTime, startTime);

		    System.err.println("Elapsed Time = " + elapsedTime);

		    if (TimeCalculator.greaterThan(elapsedTime, timeLimit)) {
			break;
		    } 
		}

		writeCount += outputPlugin.write(itemN);
	    }
	}
	
	return writeCount;
    }

}
