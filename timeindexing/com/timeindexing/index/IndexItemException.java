// IndexItemException.java

package com.timeindexing.index;

/**
 * An IndexItemException is thrown when processing the item
 * of an index fails.
 */
public class IndexItemException extends TimeIndexException {
    /**
     * Throw a IndexItemException with no message.
     */
    public IndexItemException() {
	super();
    }

    /**
     * Throw a IndexItemException with a message.
     */
    public IndexItemException(String s) {
	super(s);
    }

    /**
     * Throw a IndexItemException with an  exception from the underlying cause.
     */
    public IndexItemException(Exception e) {
	super(e);
    }

}
