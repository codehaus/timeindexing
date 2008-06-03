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



// AbstractURIPlayListItem.java

package com.timeindexing.appl.playlist;

import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.basic.Interval;
import java.net.URI;

/**
 * AbstractURIPlayListItem is an abstract base class for 
 * PlayListItems which take an Index URI rather than
 * an Index object.
 * <p>The Index object is opened on-demand, the first time 
 * getIndex() is called.
 */
public abstract class AbstractURIPlayListItem extends AbstractPlayListItem {
    // The URI for the index
    URI uri = null;

    /**
     * Get the Index for this PlayItem.
     * <p>The Index object is opened on-demand, the first time 
     * getIndex() is called.
     */
    public Index getIndex() {
	if (index == null) {
	    try {
		TimeIndexFactory factory = new TimeIndexFactory();
		index = factory.open(uri);
	    } catch (Exception e) {
		return null;
	    }
	}

	return index;
    }

    /**
     * Get the URI
     */
    public URI getURI() {
	return uri;
    }
    
    /**
     * Set the URI
     */
    public PlayListItem setURI(URI aURI) {
	uri = aURI;
	return this;
    }
    
}
