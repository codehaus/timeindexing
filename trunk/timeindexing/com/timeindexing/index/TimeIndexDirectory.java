// TimeIndexDirectory.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.RelativeAdjustableCount;

import java.util.HashMap;

/**
 * This is the TimeIndexDirectory which returns Index objects.
 *
 * It provides a directory of current Indexes, which can be looked up by name
 * or by ID.
 * This is needed in order to shared Indexes between threads.
 *
 */
public class TimeIndexDirectory {
    // Allocate the one instance of the TimeIndexDirectory
    protected static TimeIndexDirectory directory = new TimeIndexDirectory();

    /**
     * The ID directory.
     */
    protected  HashMap indexByIDDirectory = null;

    /**
     * The path directory.
     */
    protected  HashMap indexByNameDirectory = null;


    /**
     * The count hashtable.
     */
    protected  HashMap countTable = null;

    /**
     * Construct a TimeIndexDirectory
     */
    public TimeIndexDirectory() {
	indexByIDDirectory = new HashMap();
	indexByNameDirectory = new HashMap();
	countTable = new HashMap();
    }

    /**
     * Find a Index by index name.
     */
    public  ManagedIndex getIndex(String name) {
	// lookup an index using the index's name.

	if (indexByNameDirectory == null) {
	    // this shoudl never happen
	    throw new TimeIndexDirectoryException("TimeIndexFactory: The Name Directory has disappeared");
	} else {
	    ManagedIndex index = (ManagedIndex)indexByNameDirectory.get(name);

	    if (index == null) {
		return null;
	    } else {
		return index;
	    }
	}

    }

    /**
     * Save an Index by index name.
     */
    protected boolean putIndex(String name, ManagedIndex index) {
	if (indexByNameDirectory == null) {
	    // this shoudl never happen
	    throw new TimeIndexDirectoryException("TimeIndexFactory: The Name Directory has disappeared");
	} else {
	    indexByNameDirectory.put(name, index);
	    return true;
	}
    }

    /**
     * Remove an Index by index name.
     */
    protected boolean removeIndex(String name) {
	if (indexByNameDirectory == null) {
	    // this shoudl never happen
	    throw new TimeIndexDirectoryException("TimeIndexFactory: The Name Directory has disappeared");
	} else {
	    indexByNameDirectory.remove(name);
	    return true;
	}
    }

    /**
     * Find a Index by ID.
     */
    public ManagedIndex getIndex(ID anID) {
	// lookup an index using an ID.

	if (indexByIDDirectory == null) {
	    // this shoudl never happen
	    throw new TimeIndexDirectoryException("TimeIndexFactory: The ID Directory has disappeared");
	} else {
	    ManagedIndex index = (ManagedIndex)indexByIDDirectory.get(anID);

	    if (index == null) {
		return null;
	    } else {
		return index;
	    }
	}
    }

    /**
     * Save an Index by index ID
     */
    protected boolean putIndex(ID id, ManagedIndex index) {
	if (indexByIDDirectory == null) {
	    // this shoudl never happen
	    throw new TimeIndexDirectoryException("TimeIndexFactory: The ID Directory has disappeared");
	} else {
	    indexByIDDirectory.put(id, index);
	    return true;
	}
    }

     /**
     * Remove an Index by index ID
     */
    protected boolean removeIndex(ID id) {
	if (indexByIDDirectory == null) {
	    // this shoudl never happen
	    throw new TimeIndexDirectoryException("TimeIndexFactory: The ID Directory has disappeared");
	} else {
	    indexByIDDirectory.remove(id);
	    return true;
	}
    }

    /**
     * Register an Index using its name and its ID.
     */
    public boolean registerIndex(ManagedIndex index, String name, ID anID) {
	boolean result =  putIndex(name, index) && putIndex(anID, index);

	System.err.println("Registering " + name);

	// set the count to 1
	countTable.put(anID, new RelativeAdjustableCount(0));
	incrementCount(index);

	return result;
    }

    /**
     * Unregister an Index.
     */
    public boolean unregisterIndex(ManagedIndex index) {
	String name = index.getName();
	ID anID = index.getID();

	boolean result =  removeIndex(name) && removeIndex(anID);

	// remove the count for the index
	countTable.remove(anID);

	return result;
    }

    /**
     * Incrmeent the count on an Index.
     */
    public long incrementCount(ManagedIndex index) {
	ID anID = index.getID();

	// increment the count
	RelativeAdjustableCount count = (RelativeAdjustableCount)countTable.get(anID);
	count.adjust(1);

	System.err.println("Ref count to index " + index.getID() + " = " + count.value());

	return count.value();
    }

    /**
     * Decrment the count on an Index.
     */
    public long decrementCount(ManagedIndex index) {
	ID anID = index.getID();

	// decrement the count
	RelativeAdjustableCount count = (RelativeAdjustableCount)countTable.get(anID);
	count.adjust(-1);

	System.err.println("Ref count to index " + index.getID() + " = " + count.value());

	return count.value();
    }

    /*
     * Static directory methods.
     */

    /**
     *  Find a Index by index name.
     */
    public static ManagedIndex find(String name) {
	return directory.getIndex(name);
    }
    /**
     * Find a Index by ID.
     */
    public static ManagedIndex find(ID anID) {
	return directory.getIndex(anID);
    }

    /**
     * Register an Index using its name and its ID.
     */
    public static boolean register(ManagedIndex index, String name, ID anID) { 
	return directory.registerIndex(index, name, anID);
    }

    /**
     * Unregister an Index 
     */
    public static boolean unregister(ManagedIndex index) { 
	return directory.unregisterIndex(index);
    }

    /**
     * Add an extra handle on an Index.
     */
    public static long addHandle(ManagedIndex index) {
	return directory.incrementCount(index);
    }

    /**
     * Remove a handle on an Index.
     */
    public static long removeHandle(ManagedIndex index) {
	return directory.decrementCount(index);
    }

}
