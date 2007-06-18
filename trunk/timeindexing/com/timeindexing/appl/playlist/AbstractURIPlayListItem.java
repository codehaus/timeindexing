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
