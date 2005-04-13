// OutputStreamer.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.plugin.OutputPlugin;
import com.timeindexing.plugin.DefaultOutputPlugin;
import com.timeindexing.plugin.DefaultWriter;


import java.io.OutputStream;
import java.io.IOException;

/**
 * A class to output any  data
 */
public class OutputStreamer {
    protected Index index = null;
    protected OutputStream out = null;
    protected long writeCount = 0;
    protected OutputPlugin outputPlugin = null;
    protected IndexProperties outputProperties = null;

    /**
     * Construct an OutputStreamer object given
     * an index and an output stream.
     */
    public OutputStreamer(Index anIndex, OutputStream output) {
	index = anIndex;
	out = output;
	setOutputPlugin(new DefaultOutputPlugin(new DefaultWriter()));
    }

    /**
     * Construct an OutputStreamer object given
     * an index and an output stream and an OutputPlugin.
     */
    public OutputStreamer(Index anIndex, OutputStream output, OutputPlugin plugin) {
	index = anIndex;
	out = output;
	setOutputPlugin(plugin);
    }


    /**
     * Do some output, given some IndexProperties.
     * This outputs the data for the whole index.
     */
    public long doOutput(IndexProperties properties) throws IOException, TimeIndexException {
	outputProperties = properties;

	outputPlugin.setContext(index, out);

	outputPlugin.begin();
	writeCount = processTimeIndex((IndexView)index);
	outputPlugin.end();

	return writeCount;
    }

  
    /**
     * Process the TimeIndex
     */
    public long processTimeIndex(IndexView selection) throws IOException, TimeIndexException {
	// output the selection
	long writeCount = 0;
	long length = selection.getLength();

	for (long i=0; i<length; i++) {
	    IndexItem itemN = selection.getItem(i);

	    // check for refererences
	    // follow all references until we find the real data
	    while (itemN.isReference()) {
		itemN = itemN.follow();
	    }

	    writeCount += outputPlugin.write(itemN, outputProperties);
	}

	return writeCount;
    }

    /**
     * Set an output plugin, to write to output.
     */
    public OutputStreamer setOutputPlugin(OutputPlugin plugin) {
	outputPlugin = plugin;
	return this;
    }

    /**
     * Get the output plugin.
     */
    public OutputPlugin getOutputPlugin() {
	return outputPlugin;
    }


 }
