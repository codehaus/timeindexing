// Index.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.Position;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Lifetime;
import com.timeindexing.data.DataItem;

import java.util.List;
import java.util.LinkedList;

/**
 * An index.
 * This is the generic interface to an index.
 * It has all the methods for the header and for all the data requests.
 */
public interface Index extends IndexHeader {
    /**

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
     * Get the data style.
     * Either inline or external.
     */
    public int getDataStyle();


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
     * Get the index URI of a nominated index.
     */
    public String getIndexURI(ID indexID);

    /**
     * Does this index have the URI of some other index
     */
    public boolean hasIndexURI(String URIName);

    /**
     * Add a new indexID/indexURI
     * @return true, if a new index URI was added; false, if the index had this ID/URI pair already
     */
    public boolean addIndexURI(ID indexID, String URIName);

    /**
     * Add a Data Item to the Index.
     */
    public long addItem(DataItem item);

    /**
     * Add a Data Item to the Index with a speicifed Data Timestamp
     */
    public long addItem(DataItem item, Timestamp dataTime);

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n);

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p);

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime);

    /**
     * Hollow the IndexItem at the position.
     */
    public boolean hollowItem(long position);

    /**
     * Hollow the IndexItem at the position.
     */
    public boolean hollowItem(Position p);

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
     * Is the Index activated.
     */
    public boolean isActivated();

    /**
     * Make the Index activated.
     * It is not possible to add items to an Index
     * that is not active.
     */
    public Index activate();

    /**
     * Make the Index finalized.
     * It is not possible to add items ever again
     * to an Index that has been terminated.
     */
    public Index terminate();

    /**
     * Get the  last time an IndexItem was accessed from the index.
     */
    public Timestamp getLastAccessTime();

    /**
     * Flush the index.
     * Intened to sync all associated streams and files,
     * and this sets the end time too.
     */
    public boolean flush();

    /**
     * Close the index.
     * Intened to close all associated streams and files,
     * and this sets the end time too.
     */
    public boolean close();
}
