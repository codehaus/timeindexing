// AbstractManagedIndex.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.Offset;
import com.timeindexing.event.IndexPrimaryEvent;

import java.util.Set;

/**
 * An abstract index that has the managed facilities 
 * needed by the core of the system. but not accessible
 * to the application layer.
 * This is to be extended by classes that are implementations of indexes.
 */
public abstract class AbstractManagedIndex extends AbstractIndex implements ManagedIndex, ManagedIndexHeader  {
    /**
     * Set the name of the index.
     */
    public ManagedIndexHeader setName(String name) {
	header.setName(name);
	return this;
    }

    /**
     * Set the ID of the index.
     */
    public ManagedIndexHeader setID(ID id) {
	header.setID(id);
	return this;
    }

    /**
     * Set the start time
     */
    public ManagedIndexHeader setStartTime(Timestamp start) {
	header.setStartTime(start);
	return this;
    }

    /**
     * Set the end time
     */
    public ManagedIndexHeader setEndTime(Timestamp end) {
	header.setEndTime(end);
	return this;
    }


    /**
     * Set the first time
     */
    public ManagedIndexHeader setFirstTime(Timestamp first) {
	header.setFirstTime(first);
	return this;
    }

    /**
     * Set the last time
     */
    public ManagedIndexHeader setLastTime(Timestamp last) {
	header.setLastTime(last);
	return this;
    }

    /**
     * Set the first data time
     */
    public ManagedIndexHeader setFirstDataTime(Timestamp first) {
	header.setFirstDataTime(first);
	return this;
    }


    /**
     * Set the last data time
     */
    public ManagedIndexHeader setLastDataTime(Timestamp last) {
	header.setLastDataTime(last);
	return this;
    }

    /**
     * Set the no of items in the index.
     */
    public ManagedIndexHeader setLength(long length) {
	header.setLength(length);
	return this;
    }
    
    /**
     * Set the index to be terminated.
     */
    public ManagedIndexHeader setTerminated(boolean t) {
	header.setTerminated(t);
	return this;
    }

   /**
     * Set the size of the index items.
     */
    public ManagedIndexHeader setItemSize(int size) {
	header.setItemSize(size);
	return this;
    }

    /**
     * Set the size of the data items, if there is fixed size data.
     */
    public ManagedIndexHeader setDataSize(long size) {
	return this;
    }

    /**
     * Set the Offset of the fisrt item.
     */
    public ManagedIndexHeader setFirstOffset(Offset offset) {
	header.setFirstOffset(offset);
	return this;
    }

    /**
     * Set the Offset of the last item.
     */
    public ManagedIndexHeader setLastOffset(Offset offset) {
	header.setLastOffset(offset);
	return this;
    }

    /**
     * Set the type of the index.
     * Either inline or external or shadow.
     */
    public ManagedIndexHeader setIndexType(IndexType type) {
	header.setIndexType(type);
	return this;
    }

    /**
     * Set the data type of the index.
     * Some indexes have the same type throughout,
     * other have mixed type data.
     */
    public ManagedIndexHeader setIndexDataType(DataType type) {
	header.setIndexDataType(type);
	return this;
    }

    /**
     * Set the path of the index file
     */
    public ManagedIndexHeader setIndexPathName(String path) {
	header.setIndexPathName(path);
	return this;
    }


    /**
     * Set the path of the data if the index data style
     * is external or shadow.
     */
    public ManagedIndexHeader setDataPathName(String path) {
	header.setDataPathName(path);
	return this;
    }

    /**
     * Set the description.
     * This is one of the few attributes of an index that can be set directly.
     */
    public ManagedIndexHeader setDescription(Description d) {
	header.setDescription(d);
	return this;
    }

    /**
     * State that the index is not in time order any more.
     */
    public ManagedIndexHeader notInTimeOrder() {
	header.notInTimeOrder();
	return this;
    }

    /**
     * Get an option from the header.
     */
    public Object getOption(HeaderOption option) {
	return header.getOption(option);
    }

    /**
     * Set an option in the header.
     */
    public ManagedIndexHeader setOption(HeaderOption option, Object object) {
	header.setOption(option, object);
	return this;
    }

    /**
     * Does an option exist in the header.
     */
    public boolean hasOption(HeaderOption option) {
	return header.hasOption(option);
    }

    /**
     * Get the set of optional header values.
     */
    public Set listOptions() {
	return header.listOptions();
    }

    /**
     * Get all the option from the header.
     */
    public IndexProperties getAllOptions() {
	return header.getAllOptions();
    }

    /**
     * Set options in the header based on the passed IndexProperties.
     */
    public ManagedIndexHeader setOptions(IndexProperties someProperties) {
	header.setOptions(someProperties);
	return this;
    }

    /**
     * Get the headerfor the index.
     */
    public ManagedIndexHeader getHeader() {
	return header;
    }

    /**
     * Syncrhronize the values in this index header 
     * from values in a specified IndexHeader object.
     */
    public boolean syncHeader(ManagedIndexHeader indexHeader) {
	return header.syncHeader(indexHeader);
    }

    /**
     * Close this index.
     */
    public synchronized boolean close() {
	long refCount = TimeIndexDirectory.removeHandle(this);

	if (refCount == 0) {
	    System.err.println("About to really close");
	    boolean closeValue = reallyClose();

	    TimeIndexDirectory.unregister(this);

	    return closeValue;
	} else {
	    return false;
	}
    }
    

   /**
     * Really close this index.
     */
    public boolean reallyClose() {
	closed = true;
	activated = false;

	eventMulticaster.firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.CLOSED, this));

	return true;
    }

    /**
     * Try and determine if this index is alreay open
     */
    protected boolean isOpen(String name) {
	if (TimeIndexDirectory.find(name) == null) {
	    return false;
	} else {
	    return true;
	}
    }



}
