// AddItemException.java

package com.timeindexing.index;

/**
 * An AddItemException is thrown when adding an item
 * to an index fails.
 */
public class AddItemException extends IndexItemException {
    /**
     * Throw a AddItemException with no message.
     */
    public AddItemException() {
	super();
    }

    /**
     * Throw a AddItemException with a message.
     */
    public AddItemException(String s) {
	super(s);
    }

    /**
     * Throw a AddItemException with an  exception from the underlying cause.
     */
    public AddItemException(Exception e) {
	super(e);
    }

}
