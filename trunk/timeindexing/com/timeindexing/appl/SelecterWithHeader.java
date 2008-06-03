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
// SelecterWithHeader.java

package com.timeindexing.appl;

import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexException;
import java.io.OutputStream;
import java.io.IOException;


/**
 * Display a selection of a TimeIndex to an OutputStream
 * given the Index filename.
 * This always puts out a header before the selection of data from the 
 * TimeIndex.
 */
public class SelecterWithHeader extends Selecter {
    /**
     * Construct a SelecterWithHeader object.
     */
    public SelecterWithHeader(String filename, OutputStream out) {
	super(filename, out);
    }


    /**
     * Do the output
     */
    protected void output(IndexProperties selectionProperties) throws IOException, TimeIndexException {
	SelectionStreamer outputter = new SelectionWithHeaderStreamer(index, output);
	long total = 0;

	total = outputter.doOutput(selectionProperties);
    }
}
