// ManagedIndexHeader.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.Offset;
import java.util.Set;

/**
 * A managed extended index header.
 * This is the interface to an index header whic has extra attributes
 * that can be set.
 */
public interface ManagedIndexHeader extends  ExtendedIndexHeader {
    /**
     * Set the name of the index.
     */
    public ManagedIndexHeader setName(String name);

    /**
     * Set the ID of the index.
     */
    public ManagedIndexHeader setID(ID id);

    /**
     * Set the start time
     */
    public ManagedIndexHeader setStartTime(Timestamp start);

    /**
     * Set the end time
     */
    public ManagedIndexHeader setEndTime(Timestamp end);

    /**
     * Set the first time
     */
    public ManagedIndexHeader setFirstTime(Timestamp first);

    /**
     * Set the last time
     */
    public ManagedIndexHeader setLastTime(Timestamp last);

    /**
     * Set the data time of the first item
     */
    public ManagedIndexHeader setFirstDataTime(Timestamp first);

    /**
     * Set the data time of the last item
     */
    public ManagedIndexHeader setLastDataTime(Timestamp last);

    /**
     * Set the no of items in the index.
     */
    public ManagedIndexHeader setLength(long length);

    /**
     * Set the index to be terminated.
     */
    public ManagedIndexHeader terminate();

    /**
     * Set the size of the index items.
     */
    public ManagedIndexHeader setItemSize(int size);

    /**
     * Set the size of the data items, if there is fixed size data.
     */
    public ManagedIndexHeader setDataSize(long size);

    /**
     * Set the Offset of the fisrt item.
     */
    public ManagedIndexHeader setFirstOffset(Offset offset);

    /**
     * Set the Offset of the last item.
     */
    public ManagedIndexHeader setLastOffset(Offset offset);

    /**
     * Get the data style.
     * Either inline or external or shadow.
     */
    public ManagedIndexHeader setIndexType(int type);

    /**
     * Set the data type of the index.
     * Some indexes have the same type throughout,
     * other have mixed type data.
     */
    public ManagedIndexHeader setIndexDataType(DataType dataType);

    /**
     * Set the path of the index file.
     */
    public ManagedIndexHeader setIndexPathName(String path);

    /**
     * Set the path of the data if the index data style
     * is external or shadow.
     */
    public ManagedIndexHeader setDataPathName(String path);

    /**
     * State that the index is not in time order any more.
     */
    public ManagedIndexHeader notInTimeOrder();

    /**
     * Get an option from the header.
     */
    public Object getOption(HeaderOption option);

    /**
     * Does an option exist in the header.
     */
    public boolean hasOption(HeaderOption option);

    /**
     * Get the set of optional header values used in this header.
     */
    public Set listOptions();


    /**
     * Get all the option from the header.
     */
    public IndexProperties getAllOptions();

    /**
     * Set an option in the header.
     */
    public ManagedIndexHeader setOption(HeaderOption option, Object object);

    /**
     * Set options in the header based on the passed IndexProperties.
     */
    public ManagedIndexHeader setOptions(IndexProperties someProperties);

    /**
     * Syncrhronize the values in this index header 
     * from values in a specified IndexHeader object.
     */
    public boolean syncHeader(ManagedIndexHeader indexHeader);


}
