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
// IndexInputStream.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.GetItemException;
import com.timeindexing.index.IndexClosedException;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.IOException;

/**
 * A InputStream class to get access to data that
 * is in an Index, in situations where an InputStream is needed.
 */
public class IndexInputStream extends InputStream {
    Index index = null;
    long length = 0;
    long currentItem = -1;
    ByteBuffer itemdata = null;

    /*
     * Constructor for subclasses
     */
    IndexInputStream() {}

    /**
     * Construct an IndexInputStream object given
     * an Index.
     */
    public IndexInputStream(Index anIndex) {
	index = anIndex;
	length = index.getLength();
    }


    /**
     * Reads the next byte of data from the input stream. 
     * The value byte is returned as an <code>int</code> in the 
     * range <code>0</code> to
     * <code>255</code>. If no byte is available because 
     * the end of the Index
     * has been reached, the value <code>-1</code> is returned. 
     * @return     the next byte of data, or <code>-1</code>
     * if the end of the index is reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public int read() throws IOException {
	// if there is no item data or we're
	// at the nend of the bytebuffer
	// read the next IndexItem
	if (itemdata == null || itemdata.remaining() == 0) {
	    // see if we're at the end of the Index
	    if (currentItem == (length-1) ) {
		// we're at the end
		return -1;
	    } else {
		currentItem++;

		try {
		    IndexItem item = index.getItem(currentItem);
		    itemdata = item.getData();
		} catch (GetItemException gie) {
		    throw new IOException(gie.getMessage());
		} catch (IndexClosedException ice) {
		    throw new IOException(ice.getMessage());
		}
	    }
	}

	int b = (int)(itemdata.get() & 0xff);
	// return an int
	return b;
    }

}
