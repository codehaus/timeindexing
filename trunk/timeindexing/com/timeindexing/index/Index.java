// Index.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.Position;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Lifetime;
import com.timeindexing.data.DataItem;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * An index.
 * This is the generic interface to an index.
 * It has all the methods for the header and for all the data requests.
 */
public interface Index extends IndexHeader {
    /**
     * Get the size of the index items.
     */
    public int getItemSize();

    /**
     * Get the type name of the things in the data stream.
     */
    public String getDataType(ID typeID);

    /**
     * Does this index have a typed name.
     */
    public boolean hasDataType(String typeName);

    /**
     * Add a new data type
     * @return true, if a new type was added; false, if the index had this ID/typeName pair already
     */
    public boolean addDataType(ID typeID, String typeName);


    /**
     * Does this index have annotations.
     */
    public boolean hasAnnotations();

    /**
     * Get the annotation style.
     * Either inline or external.
     */
    public int getAnnotationStyle();

    /**
     * Update the description of this index.
     */
    public Index updateDescription(Description description);

    /**
     * Add a Data Item to the Index.
     */
    public long addItem(DataItem item) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException ;

    /**
     * Add a Data Item to the Index with a speicifed Data Timestamp
     */
    public long addItem(DataItem item, Timestamp dataTime) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException;

    /**
     * Add a Reference to an IndexItem in a Index.
     * The Data Timestamp of the IndexItem is passed into this Index.
     */
    public long addReference(IndexItem item, Index other) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException ;

    /**
     * Add a Reference to an IndexItem in a Index.
     * The Data Timestamp of the IndexItem is the one specified.
     */
    public long addReference(IndexItem item, Index other, Timestamp dataTime) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException;

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n) throws GetItemException;

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p) throws GetItemException;

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime) throws GetItemException;

    /**
     * Does a timestamp fall within the bounds of the Index.
     * The bounds are the first time data is put in and the last
     * time data is put in the Index.
     */
    public boolean contains(Timestamp t, IndexTimestampSelector sel);

    /**
     * Try and determine the position associated
     * with the speicifed Timestamp.
     * Returns a TimestampMapping which contains the original time
     * and the found position.
     */
    public TimestampMapping locate(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime);


    /**
     * Get the  last time an IndexItem was accessed from the index.
     */
    public Timestamp getLastAccessTime();


    /**
     * Is the Index activated.
     */
    public boolean isActivated();

    /**
     * Make the Index activated.
     * It is not possible to add items to an Index
     * that is not active.
     */
    public Index activate() throws IndexReadOnlyException, IndexWriteLockedException;

    /**
     * Make the Index finalized.
     * It is not possible to add items ever again
     * to an Index that has been terminated.
     */
    public Index terminate();

    /**
     * Flush the index.
     * Intened to sync all associated streams and files,
     * and this sets the end time too.
     */
    public boolean flush() throws IndexFlushException;

    /**
     * Is the Index closed.
     */
    public boolean isClosed();

    /**
     * Close the index.
     * Intened to close all associated streams and files,
     * and this sets the end time too.
     */
    public boolean close() throws IndexCloseException;

    /**
     * Has the index changed in any way.
     */
    public boolean isChanged();

    /**
     * Has the Index been write-locked.
     */
    public boolean isWriteLocked();

    /**
     * Get an iterator over the IndexItems in the Index.
     */
    public Iterator iterator();

    /**
     * Get a view onto the Index.
     */
    public IndexView asView();
}



