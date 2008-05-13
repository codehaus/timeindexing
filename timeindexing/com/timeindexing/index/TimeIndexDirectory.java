// TimeIndexDirectory.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.RelativeAdjustableCount;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
    protected final static TimeIndexDirectory directory = new TimeIndexDirectory();

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

    /*
     * The locks.
     */
    protected HashMap lockMap = new HashMap();

    /**
     * Construct a TimeIndexDirectory
     */
    public TimeIndexDirectory() {
	indexByIDDirectory = new HashMap();
	indexByNameDirectory = new HashMap();
	countTable = new HashMap();
	Runtime.getRuntime().addShutdownHook(new TimeIndexShutdownHook());
    }


    /**
     * Lock an index
     */
    public synchronized boolean lock(URI indexURI) {
	//System.err.println("Locking " + indexURI + " Thread " + Thread.currentThread().getName());

	if (lockMap.containsKey(indexURI)) {
	    // already locked
	    return false;
	} else {
	    // lock it
	    lockMap.put(indexURI, indexURI);
	    return true;
	}
    }

    /**
     * Unlock an index
     */
    public synchronized boolean unlock(URI indexURI) {
	//System.err.println("Unlocking " + indexURI + " Thread " + Thread.currentThread().getName());

	if (lockMap.containsKey(indexURI)) {
	    // already locked
	    lockMap.remove(indexURI);
	    notifyAll();
	    return true;
	} else {
	    // not locked
	    return false;
	}
    }


    /**
     * Is an index locked
     */
    public synchronized boolean isLocked(URI indexURI) {
	return lockMap.containsKey(indexURI);
    }

    /**
     * Wait for an index to be unlocked and ready for action.
     */
    public synchronized boolean lockWait(URI indexURI) {
	//System.err.println("lockWait " + indexURI + " Thread " + Thread.currentThread().getName());

	if (lockMap.containsKey(indexURI)) {
	    // already locked
	    while (lockMap.containsKey(indexURI)) {
		try {
		    //System.err.println("Awaiting " + indexURI + " Thread " + Thread.currentThread().getName());
		    wait();
		} catch (InterruptedException ie) {
		    //System.err.println("Return " + indexURI + " Thread " + Thread.currentThread().getName());
		}
	    }

	    // the index is unlocked
	    return true;

	} else {
	    // not locked
	    return false;
	}
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
     * List all the Indexes by name.
     */
    public Set listIndexesByName() {
	return indexByNameDirectory.keySet();
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
    public long registerIndex(ManagedIndex index, String name, ID anID) {
	boolean result =  putIndex(name, index) && putIndex(anID, index);

	//System.err.println("Registering " + name);

	// set the count to 1
	countTable.put(anID, new RelativeAdjustableCount(0));

	return incrementCount(index);
    }

    /**
     * Unregister an Index.
     */
    public boolean unregisterIndex(ManagedIndex index) {
	String name = index.getURI().toString();
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

	//System.err.println("Ref count to " + index.getURI() + " = " + count.value());

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

	//System.err.println("Ref count to " + index.getURI() + " = " + count.value());

	return count.value();
    }

    /*
     * Static directory methods.
     */

    /**
     *  Find a Index by index name.
     */
    public static ManagedIndex find(String name) {
	synchronized (directory) {
	    return directory.getIndex(name);
	}
    }
    /**
     * Find a Index by ID.
     */
    public static ManagedIndex find(ID anID) {
	synchronized (directory) {
	    return directory.getIndex(anID);
	}
    }

    /**
     * A set of all the index names.
     */
    protected static Set listIndexes() {
	// there are some shutdown situations where directory
	// is nullified before we get here
	if (directory == null) {
	    return new HashSet();
	}

	synchronized (directory) {
	    return directory.listIndexesByName();
	}
    }

    /**
     * Add an extra handle on an Index.
     */
    public static long addHandle(ManagedIndex index) {
	//TimeIndexDirectory.mem("TimeIndexDirectory");

	synchronized (directory) {
	    String indexSpec = index.getURI().toString();

	    if (directory.getIndex(indexSpec) == null) {
		// the index has not been registered yet;
		//System.err.println("TimeIndexDirectory: registering " + indexSpec + " Thread " + Thread.currentThread().getName());
		//System.err.println("TimeIndexDirectory: add handle 1 for " + indexSpec);

		return directory.registerIndex(index, indexSpec, index.getID());
	    } else {
		long handle =  directory.incrementCount(index);

		//System.err.println("TimeIndexDirectory: add handle " + handle + " for " + indexSpec + " Thread " + Thread.currentThread().getName());

		return handle;
	    }
	}
    }

    /**
     * Remove a handle on an Index.
     */
    public static long removeHandle(ManagedIndex index) {
	synchronized (directory) {
	    long count = directory.decrementCount(index);

	    //System.err.println("TimeIndexDirectory: remove  handle " + (count+1) + " for " + index.getURI().toString() + " Thread " + Thread.currentThread().getName());

	    if (count == 0) {
		//System.err.println("TimeIndexDirectory: unregistering " + index.getURI().toString() + " Thread " + Thread.currentThread().getName());
		directory.unregisterIndex(index);
	    }

	    //TimeIndexDirectory.mem("TimeIndexDirectory");

	    return count;
	}
    }

    /**
     * Gated access to the index.
     * @return true if this locks the index
     */
    public static boolean indexGate(URI indexURI) {
	synchronized (directory) {
	    if (directory.isLocked(indexURI)) {
		// the index is locked, so
		// we have to wait for it to be unlocked
		//System.err.println("lockGate waiting on lock " + indexURI + " Thread " + Thread.currentThread().getName());
		directory.lockWait(indexURI);
		return false;
	    } else {
		// it's not locked, so
		// check if it already exists
		if (directory.getIndex(indexURI.toString()) == null) {
		    // it isn't registered, so we haven't see it yet,
		    // so well lock it
		    //System.err.println("lockGate locking " + indexURI + " Thread " + Thread.currentThread().getName());
		    directory.lock(indexURI);
		    return true;
		} else {
		    // we've got it registered
		    //System.err.println("lockGate found registered " + indexURI + " Thread " + Thread.currentThread().getName());
		    return false;
		}
	    }
	}
    }

    /**
     * Unlock an index
     */
    public static boolean unlockI(URI indexURI) {
	synchronized (directory) {
	    return directory.unlock(indexURI);
	}
    }

    public static void mem(String str) {
	System.err.print(str + ": ");
	//System.err.print(Runtime.getRuntime().maxMemory());
	//System.err.print("/");
	System.err.print(Runtime.getRuntime().totalMemory()/(1024*1024));
	System.err.print("m/");
	System.err.print((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024));
	System.err.print("k+");
	System.err.print(Runtime.getRuntime().freeMemory()/(1024*1024));
	System.err.println("m");
    }



}
