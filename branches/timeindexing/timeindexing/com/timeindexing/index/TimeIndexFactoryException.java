// TimeIndexFactoryException.java

package com.timeindexing.index;

/**
 * A TimeIndexFactoryException is thrown when the TimeIndexFactory 
 * cannot resolve a value.
 */
public class TimeIndexFactoryException extends RuntimeException {
    /**
     * Throw a TimeIndexFactoryException with no message.
     */
    public TimeIndexFactoryException() {
	super();
    }

    /**
     * Throw a TimeIndexFactoryException with a message.
     */
    public TimeIndexFactoryException(String s) {
	super(s);
    }
}
