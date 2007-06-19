// FileIndexCache.java

package com.timeindexing.cache;

import com.timeindexing.time.Timestamp;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndex;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.StoredIndex;
import com.timeindexing.index.DataHolderObject;
import com.timeindexing.index.DataReference;
import com.timeindexing.index.DataReferenceObject;
import com.timeindexing.index.DataAbstraction;
import com.timeindexing.index.PositionOutOfBoundsException;
import com.timeindexing.basic.AbsolutePosition;


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
    public synchronized IndexItem getItem(long pos) {
	ManagedFileIndexItem fileItem = null;

	if (indexItems == null) {
	    return null;
	} else if (pos < 0) {
	    throw new PositionOutOfBoundsException("Index value too low: " + pos);
	    //} else if (pos >= indexItems.size()) {
	    //throw new PositionOutOfBoundsException("Index value too high: " + pos);
	} else {
	    //fileItem = (ManagedFileIndexItem)indexItems.get(pos);
	    fileItem = (ManagedFileIndexItem)indexItems.get(new AbsolutePosition(pos));

	    // call the policy
	    if (policy != null) {
		policy.notifyGetItemBegin(fileItem, pos);
	    }


	    if (fileItem.hasData()) {
		// call the policy
		if (policy != null) {
		    policy.notifyGetItemEnd(fileItem, pos);
		}

		return fileItem;

	    } else if (fileItem.isReference()) {
		// call the policy
		if (policy != null) {
		    policy.notifyGetItemEnd(fileItem, pos);
		}

		return fileItem;

	    } else {
		// call the policy
		if (policy != null) {
		    policy.notifyGetItemEnd(fileItem, pos);
		}

		return fileItem;
	    }
	}
    }

    /**
     * Hollow the IndexItem at the position.
     * This sets the data to be a data reference.
     */
    public synchronized boolean hollowItem(long pos) {
	ManagedFileIndexItem fileItem = null;

	if (indexItems == null) {
	    return false;
	} else if (pos < 0) {
	    throw new PositionOutOfBoundsException("Index value too low: " + pos);
	    //} else if (pos >= indexItems.size()) {
	    //throw new PositionOutOfBoundsException("Index value too high: " + pos);
	} else {
	    //fileItem =  (ManagedFileIndexItem)indexItems.get(pos);
	    fileItem =  (ManagedFileIndexItem)indexItems.get(new AbsolutePosition(pos));

	    if (fileItem == null) {
		// the fileItem has been removed already
		// so there's nothing to do
		return false;
	    } else {
		if (fileItem.hasData()) {
		    //System.err.println("HollowItem " + fileItem.getPosition() + " hollowing.  Offset=" + fileItem.getDataOffset() + " Size=" + fileItem.getDataSize());

		    // get the data object for this index item
		    DataHolderObject dataObj = (DataHolderObject)fileItem.getDataAbstraction();

		    // hollow it
		    DataReference dataRef = new DataReferenceObject(fileItem.getDataOffset(), fileItem.getDataSize());

		    // then set the data
		    fileItem.setData(dataRef);

		    // calculate the held volume
		    volumeHeld -= fileItem.getDataSize().value();

		    //System.err.println("Volume - = " + volumeHeld);

		    return true;
		} else {
		    //System.err.println("HollowItem " + fileItem.getPosition() + " nothing");
		    // it's already a reference
		    // do notihng
		    return false;
		}
	    }
	}
    }

    /**
     * Remove the IndexItem at the speicifed position.
     */
    public synchronized boolean removeItem(long pos) {
	if (indexItems == null || loadedMask == null) {
	    return false;
	} else if (pos < 0) {
	    throw new PositionOutOfBoundsException("Index value too low: " + pos);
	    //} else if (pos >= indexItems.size()) {
	    //throw new PositionOutOfBoundsException("Index value too high: " + pos);
	} else {
	    //System.err.println("RemoveItem " + pos + " removing");

	    //ManagedFileIndexItem fileItem =  (ManagedFileIndexItem)indexItems.get(pos);
	    ManagedFileIndexItem fileItem =  (ManagedFileIndexItem)indexItems.get(new AbsolutePosition(pos));


	    if (fileItem == null) {
		// the fileItem has been removed already
		// so there's nothing to do
		return false;
	    } else {
		// update the data volume held 
		if (fileItem.hasData()) {
		    // get the data object for this index item
		    DataHolderObject dataObj = (DataHolderObject)fileItem.getDataAbstraction();

		    // calculate the held volume
		    volumeHeld -= fileItem.getDataSize().value();

		    //System.err.println("Volume - = " + volumeHeld);
		} 

		// clear the reference to the IndexItem at position pos
		//indexItems.set(pos, null);
		indexItems.remove(new AbsolutePosition(pos));
	
		// clear the bit in the loaded map
		loadedMask.clear(pos);

		return true;
	    }
	}
    }


    /**
     * Clear the whole cache
     */
    public synchronized boolean clear() {
	long itemCount = loadedMask.size();
	long current = 0;

	while (current < itemCount) {
	    if (loadedMask.get(current) == true) {
		// the is an IndexItem loaded at position current
		// so clear it
		removeItem(current);
	    }
	}

	volumeHeld = 0;

	return true;
    }

}
