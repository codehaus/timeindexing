// IndexSpecificationException.java

package com.timeindexing.index;

/**
 * An IndexSpecificationException is thrown when the specification
 * of index properties is incomplete or illegal.
 */
public class IndexSpecificationException extends TimeIndexException {
    /**
     * Throw a IndexSpecificationException with no message.
     */
    public IndexSpecificationException() {
	super();
    }

    /**
     * Throw a IndexSpecificationException with a message.
     */
    public IndexSpecificationException(String s) {
	super(s);
    }
}
