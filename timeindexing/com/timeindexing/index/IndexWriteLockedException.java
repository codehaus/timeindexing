// IndexWriteLockedException.java

package com.timeindexing.index;

/**
 * An IndexWriteLockedException is thrown when an attempt is made
 * to activate an Index that is write-locked.
 */
public class IndexWriteLockedException extends TimeIndexException {
    /**
     * Throw a IndexWriteLockedException with no message.
     */
    public IndexWriteLockedException() {
	super();
    }

    /**
     * Throw a IndexWriteLockedException with a message.
     */
    public IndexWriteLockedException(String s) {
	super(s);
    }
}
