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



// IndexIterator.java

package com.timeindexing.index;

import java.util.Iterator;

/**
 * An iterator over the IndexItems in an Index.
 */
public class IndexIterator implements Iterator {
    // The index being iterated over
    Index index = null;

    // The current position 
    long position = 0;

    /**
     * Construct the Iterator given an Index.
     */
    protected IndexIterator(Index anIndex) {
	index = anIndex;
	position = 0;
    }

    /**
     * Does the Iterator have another element.
     */
    public boolean hasNext() {
	if (position < index.getLength()) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Get the next element.
     * @return null if it was not possible to get a particular IndexItem
     */
    public Object next() {
	try {
	    Object nextObject = index.getItem(position);

	    position++;

	    return nextObject;
	} catch (GetItemException gie) {
	    // it was not possible to get the item
	    return null;
	} catch (IndexClosedException ice) {
	    // it was not possible to get the item
	    return null;
	} 
    }

    /**
     * Remove an element.
     * This is not supported, so an UnsupportedOperationExceptino is thrown
     */
    public void remove() {
	throw new UnsupportedOperationException("Index Iterators do not support remove()");
    }
	
}
