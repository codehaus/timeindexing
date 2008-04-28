// IndexSelectionInputStream.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.GetItemException;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.IOException;

/**
 * A InputStream class to get to data in an Index.
 */
public class IndexSelectionInputStream extends IndexInputStream {
    /**
     * Construct an IndexSelectionInputStream object given
     * an Index and some IndexProperties
     */
    public IndexSelectionInputStream(Index anIndex, IndexProperties properties) {
	SelectionProcessor selector = new SelectionProcessor();
 
	IndexView selection = selector.select((IndexView)anIndex, properties);
	index = selection;
	length = selection.getLength();
    }
}
