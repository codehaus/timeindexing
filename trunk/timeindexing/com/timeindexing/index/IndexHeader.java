// IndexHeader.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.ID;
import java.net.URI;

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
     * Get the Index specification in the form of a URI.
     */
    public URI getURI();

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
     * @return ZeroTimestamp if there is no first item, usually when there are no items in the index
     */
    public Timestamp getFirstTime();

    /**
     * Get the time the last IndexItem was put into the Index.
     * @return ZeroTimestamp if there is no last item, usually when there are no items in the index
     */
    public Timestamp getLastTime();

    /**
     * Get the data time for the first IndexItem in the Index.
     * @return ZeroTimestamp if there is no first item, usually when there are no items in the index
     */
    public Timestamp getFirstDataTime();

    /**
     * Get the data time for the last IndexItem in the Index.
     * @return ZeroTimestamp if there is no last item, usually when there are no items in the index
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
     * Get the  style of the index.
     * Either inline or external or shadow or incore.
     */
    public IndexType getIndexType();

    /**
     * Get the data type of the index.
     * Some indexes have the same type throughout,
     * other have mixed type data.
     */
    public DataType getIndexDataType();

    /**
     * Get the path of the index.
     * @return null if there is no index path
     */
    public String getIndexPathName();

    /**
     * Get the path of the data if the index data style
     * is external or shadow.
     * @return null if there is no data path
     */
    public String getDataPathName();

    /**
     * Get the description for an index.
     * @return null if there is no description
     */
    public Description getDescription();

    /**
     * Is the Index only available for read-only operations.
     */
    public boolean isReadOnly();

    /**
     * Is the Index terminated.
     */
    public boolean isTerminated();

    /**
     * Is the index still in time order.
     */
    public boolean isInTimeOrder();
}
