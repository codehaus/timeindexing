// IndexCloseException.java

package com.timeindexing.index;

/**
 * An IndexCloseException is thrown when doing a close()
 * on an Index fails.
 */
public class IndexCloseException extends TimeIndexException {
    /**
     * Throw a IndexCloseException with no message.
     */
    public IndexCloseException() {
	super();
    }

    /**
     * Throw a IndexCloseException with a message.
     */
    public IndexCloseException(String s) {
	super(s);
    }
}
