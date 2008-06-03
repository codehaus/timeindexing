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
// PlayList.java

package com.timeindexing.appl.playlist;

import java.util.LinkedList;
import java.util.Iterator;

/**
 * A play list.
 * It contains a list of PlayListItems.
 */
public class PlayList {
    LinkedList list = null;

    /**
     * Construct a PlayList.
     */
    public PlayList() {
	list = new LinkedList();
    }

    /**
     * Add a PlayItem to the end of the PlayList.
     */
    public void add(PlayListItem item) {
	list.add(item);
    }

    /**
     * Add a PlayItem at the specified position in the PlayList.
     */
    public void add(int pos, PlayListItem item) {
	list.add(pos, item);
    }

    /**
     * Get the nth element of the PlayList.
     */
    public PlayListItem get(int pos) {
	return (PlayListItem)list.get(pos);
    }

    /**
     * Get the size of the PlayList.
     */
    public int size() {
	return list.size();
    }

    /**
     * Get an iterator over the PlayList
     */
    public Iterator iterator() {
	return list.iterator();
    }

    /**
     * Create a String view of a PlayList.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(256);

	buffer.append("PlayList: ");
	buffer.append(hashCode());
	buffer.append(" size = ");
	buffer.append(size());
	buffer.append('\n');

	if (size() > 0) {
	    // show the PlayListItems
	    Iterator itemsI = list.iterator();

	    while (itemsI.hasNext()) {
		PlayListItem item = (PlayListItem)itemsI.next();

		buffer.append(item.toString());
		buffer.append('\n');
	    }
	}

	return buffer.toString();
    }
}
