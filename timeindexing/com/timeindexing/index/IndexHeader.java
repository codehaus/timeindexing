// IndexHeader.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.ID;

/**
 * An index header.
 * This is the generic interface to an index header.
 */
public interface IndexHeader {
    /**
     * The name of the index.
     */
    public String getName();

    /**
     * Get an ID of an index.
     */
    public ID getID();

     /**
      * Get the start time of the index.
      * This is when the index was created not necessarliy when the first item
      * was added to the index.
      */
     public Timestamp getStartTime();

     /**
      * Get the end time of the index.
      * This is the time the last item was closed, not necessarliy when the last item
      * was added to the index.
      */
     public Timestamp getEndTime();

    /**
     * Get the time the first IndexItem was put into the Index.
     */
    public Timestamp getFirstTime();

    /**
     * Get the time the last IndexItem was put into the Index.
     */
    public Timestamp getLastTime();

    /**
     * Get the data time for the first IndexItem in the Index.
     */
    public Timestamp getFirstDataTime();

    /**
     * Get the data time for the last IndexItem in the Index.
     */
    public Timestamp getLastDataTime();

    /**
     * Does the index have fixed size data.
     */
    public boolean isFixedSizeData() ;

    /**
     * Get the size of the data items, if there is fixed size data.
     * The value -1 means variable sized data.
     */
    public long getDataSize();

    /**
     * Get the no of items in the index.
     */
    public long getLength();

    /**
     * Is the Index terminated.
     */
    public boolean isTerminated();
}
