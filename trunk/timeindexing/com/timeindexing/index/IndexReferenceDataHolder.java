// IndexReferenceDataHolder.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.Size;
import com.timeindexing.basic.Position;
import com.timeindexing.time.Timestamp;
import java.util.Properties;
import java.nio.ByteBuffer;
import java.net.URI;


/**
 * A class that refers to an IndexItem in another Index.
 */
public class IndexReferenceDataHolder implements IndexReference, DataHolder {
    // The IndexItem this IndexReference is in
    ManagedIndexItem indexItem = null;

    // The ID of the index being rferred to
    ID otherIndexID = null;

    // The position of the IndexItem
    Position indexItemPosition = null;

    Timestamp lastAccessTime = null;
    Timestamp readTime = null;

    final static ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
    final static Size ZERO = new Size(0);

    /**
     * Construct an IndexReferenceDataHolder given
     * the IndexItem this is held in,
     * the ID of the Index to refer to and the Position of an IndexItem to refer to.
     */
    public IndexReferenceDataHolder(ManagedIndexItem myIndexItem, ID indexID, Position itemPosition) {
	indexItem = myIndexItem;
	otherIndexID = indexID;
	indexItemPosition = itemPosition;
    }

    /**
     * Construct an IndexReferenceDataHolder given the URI of the Index to refer to,
     * the ID of the Index to refer to, and the Position of an IndexItem to refer to.
     */
    public IndexReferenceDataHolder(ID indexID, Position itemPosition) {
	otherIndexID = indexID;
	indexItemPosition = itemPosition;
    }

    /**
     * The URI of the index being referenced.
     */
    public URI getIndexURI() {
	return ((ManagedIndex)indexItem.getIndex()).getIndexURI(otherIndexID);
    }

    /**
     * The ID of the index being referenced.
     */
    public ID getIndexID() {
	return otherIndexID;
    }

    /**
     * The Position of the IndexItem being referenced.
     */
    public Position getIndexItemPosition() {
	return indexItemPosition;
    }

    /**
     * Follow this reference.
     */
    public IndexItem follow() throws GetItemException {
	return indexItem.follow();
    }

    /**
     * Get the data itself.
     * This should follow the Reference.
     */
    public ByteBuffer getBytes() {
	return EMPTY_BUFFER;
	/*
	try {
	    IndexItem otherItem = follow();
	    return otherItem.getData();
	} catch (GetItemException gie) {
	    return null;
	}
	*/
    }

    /**
     * Get the size of the data.
     * This should follow the Reference.
     */
    public Size getSize() {
	return ZERO;
	/*
	  try {
	    IndexItem otherItem = follow();
	    return otherItem.getDataSize();
	} catch (GetItemException gie) {
	    return null;
	}
	*/}

    /**
     * Get the last time this object was accessed.
     */
    public Timestamp getLastAccessTime() {
	return lastAccessTime;
    }

    /**
     * Get the time the data was read from storage into this object.
     */
    public Timestamp getReadTime() {
	return readTime;
    }


    /**
     * Get the IndexItem this IndexReference is associated with.
     */
    public IndexItem getIndexItem() {
	return indexItem;
    }

    /**
     * Set the IndexItem this IndexReference is associated with.
     */
    public IndexReferenceDataHolder setIndexItem(IndexItem item) {
	indexItem = (ManagedIndexItem)item;
	return this;
    }
}

