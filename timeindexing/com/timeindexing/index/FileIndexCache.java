// FileIndexCache.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;

import java.util.List;
import java.util.LinkedList;

/**
 * The implementation of a cache which holds the index items
 * for file indexes.
 */
public class FileIndexCache extends DefaultIndexCache implements IndexCache {
    /**
     * Create a FileIndexCache object.
     */
    public FileIndexCache(StoredIndex index) {
	super((ManagedIndex)index);
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n) {
	IndexItem item = super.getItem(n);

	ManagedFileIndexItem fileItem = (ManagedFileIndexItem)item;

	if (fileItem.hasData()) {
	    return item;
	} else {
	    // get the DataAbstraction, which must be a DataReference, and
	    // which holds the offset and the size
	    DataReference dataRef = (DataReference)fileItem.getDataAbstraction();

	    // read the data
	    DataHolderObject dataObj = ((StoredIndex)myIndex).readData(dataRef);

	    // if we got the data 
	    if (dataObj != null) {
		// then set the data
		fileItem.setData(dataObj);
	    }

	    return item;
	}
    }
    /**
     * Hollow the IndexItem at the position.
     * This sets the data to be a data reference.
     */
    public boolean hollowItem(long position) {
	super.hollowItem(position);

	ManagedFileIndexItem fileItem = null;

	if (indexItems == null) {
	    return false;
	} else {
	    fileItem =  (ManagedFileIndexItem)indexItems.get(position);

	    if (fileItem.hasData()) {
		DataHolderObject dataObj = (DataHolderObject)fileItem.getDataAbstraction();
		// hollow it
		DataReference dataRef = new DataReferenceObject(fileItem.getDataOffset(), fileItem.getDataSize());

		// then set the data
		fileItem.setData(dataRef);

		return true;
	    } else {
		// it's already a reference
		// do notihng
		return false;
	    }
	}
    }
}
