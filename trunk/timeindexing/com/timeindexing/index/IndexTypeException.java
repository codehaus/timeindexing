// IndexTypeException.java

package com.timeindexing.index;

/**
 * A IndexTypeException is thrown when an Index
 * cannot be typed.
 * This usually because the file is not actually an index file.
 */
public class IndexTypeException extends TimeIndexException {
    /**
     * Throw a IndexTypeException with no message.
     */
    public IndexTypeException() {
	super();
    }

    /**
     * Throw a IndexTypeException with a message.
     */
    public IndexTypeException(String s) {
	super(s);
    }
}
