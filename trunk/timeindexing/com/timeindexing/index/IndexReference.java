// IndexReference.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.Position;
import java.net.URI;

/**
 * A reference to an IndexItem in another Index.
 */
public interface IndexReference {
    /**
     * The URI of Index being referenced.
     */
    public URI getIndexURI();

    /**
     * The ID of the  Index being referenced.
     */
    public ID getIndexID();

    /**
     * The Position of the IndexItem being referenced.
     */
    public Position getIndexItemPosition();

    /**
     * Follow this reference.
     */
    public IndexItem follow() throws GetItemException;
} 
