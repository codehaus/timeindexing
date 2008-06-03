Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
import com.timeindexing.event.OutputEvent;
import com.timeindexing.event.OutputEventListener;
import com.timeindexing.event.OutputEventGenerator;

import java.io.OutputStream;
import java.io.IOException;


/**
 * A class to output any  data
 */
public class OutputStreamer extends OutputEventGenerator {
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
    public long processTimeIndex(IndexView index) throws IOException, TimeIndexException {
	// output the selection
	long writeCount = 0;
	long writeTotal = 0;
	long length = index.getLength();

	for (long i=0; i<length; i++) {
	    IndexItem itemN = fetchIndexItem(i, index);

	    writeCount = outputPlugin.write(itemN, outputProperties);
	    writeTotal += writeCount;

	    if (hasOutputEventListeners()) {
		fireOutputEvent(new OutputEvent(index.getURI().toString(), index.getID(), writeCount, this));
	    }
	}

	writeCount = outputPlugin.flush();
	writeTotal += writeCount;

	if (hasOutputEventListeners()) {
	    fireOutputEvent(new OutputEvent(index.getURI().toString(), index.getID(), writeCount, this));
	}

	return writeTotal;
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

    /**
     * Fetch a single item from an Index, ready for outputting.
     * This will follow all references to get to the data.
     */
    protected IndexItem fetchIndexItem(long pos, Index index) throws IOException, TimeIndexException {
	IndexItem itemN = index.getItem(pos);

	// check for refererences
	// follow all references until we find the real data
	while (itemN.isReference()) {
	    itemN = itemN.follow();
	}


	return itemN;
    }


 }
