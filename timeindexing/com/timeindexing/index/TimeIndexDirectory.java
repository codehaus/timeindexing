// TimeIndexDirectory.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;

import java.util.Properties;
import java.util.HashMap;
import java.io.File;


/**
 * This is the TimeIndexDirectory which returns Index objects.
 *
 * It provides a directory of current Indexes, which can be looked up by name
 * or by ID.
 * This is needed in order to shared Indexes between threads.
 *
 */
public class TimeIndexDirectory {
    protected static TimeIndexDirectory directory = new TimeIndexDirectory();

    /**
     * The ID directory.
     */
    protected  HashMap indexByIDDirectory = null;

    /**
     * The name directory.
     */
    protected  HashMap indexByNameDirectory = null;


    /**
     * Construct a TimeIndexDirectory
     */
    public TimeIndexDirectory() {
	indexByIDDirectory = new HashMap();
	indexByNameDirectory = new HashMap();
    }

    /**
     * Find a Index by index name.
     */
    public  Index getIndex(String name) {
	// lookup an index using the index's name.

	if (indexByNameDirectory == null) {
	    // this shoudl never happen
	    throw new TimeIndexDirectoryException("TimeIndexFactory: The Name Directory has disappeared");
	} else {
	    Index index = (Index)indexByNameDirectory.get(name);

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
    protected boolean putIndex(String name, Index index) {
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
    public Index getIndex(ID anID) {
	// lookup an index using an ID.

	if (indexByIDDirectory == null) {
	    // this shoudl never happen
	    throw new TimeIndexDirectoryException("TimeIndexFactory: The ID Directory has disappeared");
	} else {
	    Index index = (Index)indexByIDDirectory.get(anID);

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
    protected boolean putIndex(ID id, Index index) {
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
    public boolean registerIndex(Index index, String name, ID anID) {
	boolean result =  putIndex(name, index) && putIndex(anID, index);

	return result;
    }

    /**
     * Unregister an Index.
     */
    public boolean unregisterIndex(Index index) {
	String name = index.getName();
	ID anID = index.getID();

	boolean result =  removeIndex(name) && removeIndex(anID);

	return result;
    }

    /*
     * Static directory methods.
     */

    /**
     *  Find a Index by index name.
     */
    public static Index find(String name) {
	return directory.getIndex(name);
    }
    /**
     * Find a Index by ID.
     */
    public static Index find(ID anID) {
	return directory.getIndex(anID);
    }

    /**
     * Register an Index using its name and its ID.
     */
    public static boolean register(Index index, String name, ID anID) { 
	return directory.registerIndex(index, name, anID);
    }

    /**
     * Unregister an Index 
     */
    public static boolean unregister(Index index) { 
	return directory.unregisterIndex(index);
    }
}
