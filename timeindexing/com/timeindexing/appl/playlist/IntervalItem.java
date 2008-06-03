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
