// IndexOpenException.java

package com.timeindexing.index;

/**
 * A IndexOpenException is thrown when an Index
 * cannot be opend.
 * This usually because the Properties passed at open time
 * do not have enough information, or the index files do not exist,
 * or the file is not an index.
 */
public class IndexOpenException extends TimeIndexException {
    // root cause
    Exception cause = null;

    /**
     * Throw a IndexOpenException with no message.
     */
    public IndexOpenException() {
	super();
    }

    /**
     * Throw a IndexOpenException with a message.
     */
    public IndexOpenException(String s) {
	super(s);
    }

    /**
     * Throw a IndexOpenException with an  exception from the underlying cause.
     */
    public IndexOpenException(Exception e) {
	super(e);
    }


}
