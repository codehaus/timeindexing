// IndexActivationException.java

package com.timeindexing.index;

/**
 * An IndexActivationException is thrown when an attempt is made
 * to add data to a activation index.
 */
public class IndexActivationException extends TimeIndexException {
    /**
     * Throw a IndexActivationException with no message.
     */
    public IndexActivationException() {
	super();
    }

    /**
     * Throw a IndexActivationException with a message.
     */
    public IndexActivationException(String s) {
	super(s);
    }
}
