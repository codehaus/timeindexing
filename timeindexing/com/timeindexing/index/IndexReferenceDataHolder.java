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



// IndexReferenceDataHolder.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.Size;
import com.timeindexing.basic.Position;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Clock;
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
	readTime = Clock.time.time();
	lastAccessTime = Timestamp.ZERO;
    }

    /**
     * Construct an IndexReferenceDataHolder given the URI of the Index to refer to,
     * the ID of the Index to refer to, and the Position of an IndexItem to refer to.
     */
    public IndexReferenceDataHolder(ID indexID, Position itemPosition) {
	otherIndexID = indexID;
	indexItemPosition = itemPosition;
	readTime = Clock.time.time();
	lastAccessTime = Timestamp.ZERO;
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
    public IndexItem follow() throws GetItemException, IndexClosedException {
	lastAccessTime = Clock.time.time();
	return indexItem.follow();
    }

    /**
     * Get the data itself.
     * This should follow the Reference.
     */
    public ByteBuffer getBytes() {
	lastAccessTime = Clock.time.time();

	// WAS return EMPTY_BUFFER;

	try {
	    IndexItem otherItem = follow();
	    return otherItem.getData();
	} catch (GetItemException gie) {
	    return null;
	} catch (IndexClosedException ice) {
	    return null;
	}
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

