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
// AbstractPlayListItem.java

package com.timeindexing.appl.playlist;

import com.timeindexing.index.Index;
import com.timeindexing.basic.Interval;

/**
 * AbstractPlayListItem is an abstract base class for all PlayItems.
 */
public abstract class AbstractPlayListItem implements PlayListItem {
    // The Index this PlayListItem refers to
    Index index = null;

    // The Interval this PlayListItem refers to
    Interval interval = null;

    /**
     * Get the Index for this PlayItem.
     */
    public Index getIndex() {
	return index;
    }

    /**
     * Set the Index for this PlayItem.
     */
    public PlayListItem setIndex(Index anIndex) {
	index = anIndex;
	return this;
    }

    /**
     * Get the Interval for this PlayItem.
     */
    public Interval getInterval() {
	return interval;
    }

    /**
     * Set the Interval for this PlayItem.
     */
    public PlayListItem setInterval(Interval anInterval) {
	interval = anInterval;
	return this;
    }
}
