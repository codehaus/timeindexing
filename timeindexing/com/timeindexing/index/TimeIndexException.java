// TimeIndexException.java

package com.timeindexing.index;

/**
 * A TimeIndexException is a generic base exception
 * for all Exceptions in the TimeIndex system.
 */
public class TimeIndexException extends Exception {
    /**
     * Throw a TimeIndexException with no message.
     */
    public TimeIndexException() {
	super();
    }

    /**
     * Throw a TimeIndexException with a message.
     */
    public TimeIndexException(String s) {
	super(s);
    }

    /**
     * Throw a TimeIndexException with an exception
     */
    public TimeIndexException(Throwable t) {
	super(t);
    }
}
