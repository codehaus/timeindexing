//ManagedIndex.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.Offset;
import com.timeindexing.event.IndexEventGenerator;
import java.util.Properties;
import java.util.Collection;
import java.net.URI;

/**
 * An interface for classes that need to manage
 * Indexes.
 * It has the methods needed to manage an index,
 * but are not needed by the application  layer.
 */
public interface ManagedIndex extends ExtendedIndex, ManagedIndexHeader, IndexEventGenerator  {
    /**
     * Open this index.
     */
     public boolean open(Properties props) throws IndexSpecificationException, IndexOpenException;

    /**
     * Create this index.
     */
     public boolean create(Properties props) throws IndexSpecificationException, IndexCreateException;

    /**
     * Add a Referemnce to an IndexItem in a Index.
     * This version takes an IndexReference and a Data Timestamp.
     * It is used internally when doing a TimeIndexFactory.save().
     */
    public IndexItem addReference(IndexReference reference, Timestamp dataTS) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException;

    /**
     * Track a Referenced Index.
     */
    public int trackReferencedIndex(Index index);

    /**
     * Is an Index being tracked
     */
    public boolean isTrackingIndex(ID indexID);

    /**
     * Get an Index being tracked
     */
    public Index getTrackedIndex(ID indexID) ;

    /**
     * List all the tracked Indexes.
     */
    public Collection listTrackedIndexes();

    /**
     * Close this index.
     */
    public boolean reallyClose() throws IndexCloseException ;

    /**
     * Get the headerfor the index.
     */
    public ManagedIndexHeader getHeader();

    
    /**
     * Add a view to this index.
     * @return the IndexView itself
     */
    public IndexView addView();

    /**
     * Remove a view from this index.
     * @return How many views are still left on the Index
     */
    public long removeView();


}
