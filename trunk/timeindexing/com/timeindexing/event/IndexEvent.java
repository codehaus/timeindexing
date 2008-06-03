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



// IndexEvent.java

package com.timeindexing.event;

import com.timeindexing.basic.ID;
import java.util.EventObject;

/**
 * An Index Event, which is generated at key situations
 * through an Index's lifetime.
 */
public abstract class IndexEvent extends EventObject {
    /*
     * The nae of the Index.
     */
    String name = null;

    /*
     * The ID of the Index.
     */
    ID theID = null;

    /**
     * Construct an IndexEvent.
     * Constructed from an index name, an index ID,
     * and the source of the event.
     */
    public IndexEvent(String aName, ID anID, Object aSource) {
	super(aSource);
	name = aName;
	theID = anID;
    }

    /**
     * Get the name of the index.
     */
    public String getName() {
	return name;
    }

    /**
     * Get an ID of an index.
     */
    public ID getID() {
	return theID;
    }

    /**
     * Get the source of the event.
     */
    public Object getSource() {
	return source;
    }

}
