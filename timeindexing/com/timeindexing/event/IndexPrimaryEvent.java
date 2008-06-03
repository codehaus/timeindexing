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
// IndexPrimaryEvent.java

package com.timeindexing.event;

import com.timeindexing.basic.ID;

/**
 * An Index Primary Event, which is generated 
 * at primary moments.
 * These include: open , close, committed, create, add a view, remove a view.
 */
public class IndexPrimaryEvent extends IndexEvent {
    /**
     * Used when an Index is opened.
     */
    public static final int OPENED        = 1;

    /**
     * Used when an Index is closed.
     */
    public static final int CLOSED       = 2;

    /**
     * Used when an Index is committed.
     */
    public static final int COMMITTED    = 3;

    /**
     * Used when an Index is created.
     */
    public static final int CREATED    = 4;

    /**
     * Used whe na new view is added.
     */
    public static final int ADD_VIEW = 5;

    /**
     * Used whe na new view is removed.
     */
    public static final int REMOVE_VIEW = 6;


    /*
     * The specifier for this event
     */
    int eventSpecifier = 0;

    /**
     * Construct an IndexPrimaryEvent.
     * Constructed from an index name, an index ID, an event specifier,
     * and the source of the event.
     */
    public IndexPrimaryEvent(String aName, ID anID, int eventSpec, Object aSource) {
	super(aName, anID, aSource);
	eventSpecifier = eventSpec;
    }

    /**
     * Get the event specifier.
     */
    public int getEventSpecifier() {
	return eventSpecifier;
    }
}
