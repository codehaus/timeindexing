// TimeIndexDirectoryException.java

package com.timeindexing.index;

/**
 * A TimeIndexDirectoryException is thrown when the TimeIndexDirectory 
 * cannot resolve a value.
 */
public class TimeIndexDirectoryException extends RuntimeException {
    /**
     * Throw a TimeIndexDirectoryException with no message.
     */
    public TimeIndexDirectoryException() {
	super();
    }

    /**
     * Throw a TimeIndexDirectoryException with a message.
     */
    public TimeIndexDirectoryException(String s) {
	super(s);
    }
}
