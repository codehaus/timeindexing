// IndexClosedException.java

package com.timeindexing.index;

/**
 * An IndexClosedException is thrown when an attempt is made
 * to add data to a closed index.
 */
public class IndexClosedException extends TimeIndexException {
    /**
     * Throw a IndexClosedException with no message.
     */
    public IndexClosedException() {
	super();
    }

    /**
     * Throw a IndexClosedException with a message.
     */
    public IndexClosedException(String s) {
	super(s);
    }
}
