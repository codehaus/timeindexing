// IndexTerminatedException.java

package com.timeindexing.index;

/**
 * An IndexTerminatedException is thrown when an attempt is made
 * to add data to a terminated index.
 */
public class IndexTerminatedException extends RuntimeException {
    /**
     * Throw a IndexTerminatedException with no message.
     */
    public IndexTerminatedException() {
	super();
    }

    /**
     * Throw a IndexTerminatedException with a message.
     */
    public IndexTerminatedException(String s) {
	super(s);
    }
}
