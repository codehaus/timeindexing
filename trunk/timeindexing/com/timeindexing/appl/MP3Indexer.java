// MP3Indexer.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.NanosecondTimestamp;
import com.timeindexing.time.NanosecondElapsedFormat;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.plugin.InputPlugin;
import com.timeindexing.plugin.DefaultInputPlugin;
import com.timeindexing.plugin.MP3;
import com.timeindexing.plugin.ReaderPlugin;
import com.timeindexing.plugin.ReaderResult;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.ReaderResultItem;

import java.io.InputStream;
import java.io.IOException;

/**
 * A class to index MP3 data.
 * All data times in the index are elapsed times.
 */
public class MP3Indexer {
    Index index = null;
    InputStream in = null;
    InputPlugin inputPlugin = null;
    ReaderPlugin readerPlugin = null;

    /**
     * Construct an InputStreamer object given
     * an index and an input stream.
     */
    public MP3Indexer(Index anIndex, InputStream input) {
	index = anIndex;
	in = input;
	readerPlugin = new MP3(input);
	inputPlugin = new DefaultInputPlugin(index,input, readerPlugin);
    }

    /**
     * Do some input, given some IndexProperties.
     * This inputs the data for the whole index.
     * @return the no of items read.
     */
    public long doInput(IndexProperties properties) throws IOException, TimeIndexException {
	long readCount = 0;

	inputPlugin.begin();
	readCount = processInput();
	inputPlugin.end();

	return readCount;
    }

    /**
     * Read the input.
     */
    protected long processInput()  throws IOException, TimeIndexException {
	Timestamp dataTS = null;
	Timestamp prevDataTS = new NanosecondTimestamp(0);
	Timestamp difference = null;
	DataItem item = null;
	ReaderResult result = null;
	long indexSize = 0;
	int count = 0;

	System.err.print(new NanosecondElapsedFormat().dayFormat(prevDataTS));
	System.err.print('\r');

	// do stuff
	while ((result = inputPlugin.read()) != null) {

	    dataTS = result.getDataTimestamp();

	    difference = TimeCalculator.subtractTimestamp(dataTS, prevDataTS);

	    // print the times occassionally, once a second-ish
	    if (difference.getSeconds() >= 1) {
		System.err.print(new NanosecondElapsedFormat().dayFormat(dataTS));
		System.err.print('\r');
		prevDataTS = dataTS;
	    }

	    item = new ReaderResultItem(result);

	    indexSize = index.addItem(item, dataTS);
	}

	System.err.print('\n');

	// terminate the index
	index.terminate();

	// end it
	index.close();

	return indexSize;
    }
}
