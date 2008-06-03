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
// IndexAddEvent.java

package com.timeindexing.event;

import com.timeindexing.index.IndexItem;
import com.timeindexing.basic.ID;

/**
 * An Index Add Event, which is generated 
 * when an IndexItem is added to an Index.
 */
public class IndexAddEvent extends IndexEvent {
    /*
     * The IndexItem which was added and passed in this event.
     */
    IndexItem item = null;

    /**
     * Construct an IndexAddEvent.
     * Constructed from an index name, an index ID,
     * the IndexItem, and the source of the event.
     */
    public IndexAddEvent(String aName, ID anID, IndexItem anItem, Object aSource) {
	super(aName, anID, aSource);
	item = anItem;
    }

    /**
     * Get the IndexItem from the event.
     */
    public IndexItem getIndexItem() {
	return item;
    }
     

    
}
