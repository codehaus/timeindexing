// GetItemException.java

package com.timeindexing.index;

/**
 * An GetItemException is thrown when getting an item
 * from an index fails.
 */
public class GetItemException extends IndexItemException {
    /**
     * Throw a GetItemException with no message.
     */
    public GetItemException() {
	super();
    }

    /**
     * Throw a GetItemException with a message.
     */
    public GetItemException(String s) {
	super(s);
    }

    /**
     * Throw a GetItemException with an  exception from the underlying cause.
     */
    public GetItemException(Exception e) {
	super(e);
    }

}
