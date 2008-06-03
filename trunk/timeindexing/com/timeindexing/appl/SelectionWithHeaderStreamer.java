/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



// SelectionWithHeaderStreamer.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.appl.SelectionProcessor;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A class to output a selction of the data toghether with a header.
 */
public class SelectionWithHeaderStreamer extends SelectionStreamer  {
    /**
     * Construct an SelectionWithHeaderStreamer object given
     * an index and an output stream.
     */
    public SelectionWithHeaderStreamer(Index anIndex, OutputStream output) {
	super(anIndex, output);
    }

    /**
     * Do some output, given some IndexProperties.
     * The IndexProperties specify a selection to make.
     * Only the selection is output.
     */
    public long doOutput(IndexProperties properties) throws IOException, TimeIndexException {
	outputProperties = properties;

	outputPlugin.setContext(index, out);

	outputPlugin.begin();

	// output the header
	writeCount += processHeader(index);


	// output main selection
	SelectionProcessor selector = new SelectionProcessor();
 
	IndexView selection = selector.select((IndexView)index, properties);

	writeCount += processTimeIndex((IndexView)selection);

	System.err.println("SelectionWithHeaderStreamer: " + this.hashCode() + " wrote = " + writeCount + ". Thread " + Thread.currentThread().getName() );
	outputPlugin.end();

	return writeCount;
    }

    /**
     * Process the header and output it.
     * @return the no of bytes output
     */
    protected long processHeader(Index index)  throws IOException, TimeIndexException {
	long writeCount = 0;

	// get the header
	IndexItem header = fetchIndexItem(0, index);

	// output the header
	writeCount = outputPlugin.write(header, outputProperties);

	return writeCount;
    }
}
