// IndexFlushException.java

package com.timeindexing.index;

/**
 * An IndexFlushException is thrown when doing a flush()
 * on an Index fails.
 */
public class IndexFlushException extends TimeIndexException {
    /**
     * Throw a IndexFlushException with no message.
     */
    public IndexFlushException() {
	super();
    }

    /**
     * Throw a IndexFlushException with a message.
     */
    public IndexFlushException(String s) {
	super(s);
    }
}
