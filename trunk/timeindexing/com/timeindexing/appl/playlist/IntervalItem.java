// IntervalItem.java

package com.timeindexing.appl.playlist;

import com.timeindexing.index.Index;
import com.timeindexing.basic.Interval;

/**
 * A PlayListItem which is constructed from an Index
 * an Interval.
 */
public class IntervalItem extends AbstractPlayListItem {
    /**
     * Construct the IntervalItem
     * @param index the Index 
     * @param interval the Interval
     */
    public IntervalItem(Index index, Interval interval) {
	setIndex(index);
	setInterval(interval);
    }

    /**
     * Create a String view of this PlayListItem.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(128);

	buffer.append(getIndex().getURI().toString());
	buffer.append(" => ");
	buffer.append(getInterval());

	return buffer.toString();
    }
}
