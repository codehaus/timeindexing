// UID.java

package com.timeindexing.basic;

import java.io.Serializable;

/**
 * This represents a Unique ID.
 * The constructor generates an ID which cannot be changed.
 */

public class UID extends AID implements ID, Serializable {
    /**
     * An ID generator.
     */
    private synchronized static long generate(UID forWhom) {
	// 1 day = 86400000 u Sec
	// allow 42 bits for time sunce 01/01/1970
	// this is enough for 50903 days  =~ 139 years
	// so give 22 bits for other data
	return  System.currentTimeMillis() + ((long)forWhom.superHash() << 42) ;
    }

    /**
     * Create a new ID object.
     * This will be initialised with a unique ID.
     */
    public UID() {
	idValue = UID.generate(this);
    }

    /**
     * Determine what the hash code would have been if
     * it had not been over ridden here.
     */
    /**
     * Determine what the hash code would have been if
     * it had not been over ridden here.
     */
    private int superHash() {
	return System.identityHashCode(this);
    }

    /*
      private int superHash() {
      return super.hashCode();
      }
    */

}


