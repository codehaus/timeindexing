// IndexReadOnlyException.java

package com.timeindexing.index;

/**
 * An IndexReadOnlyException is thrown when an attempt is made
 * to activate an Index that is read-only.
 */
public class IndexReadOnlyException extends TimeIndexException {
    /**
     * Throw a IndexReadOnlyException with no message.
     */
    public IndexReadOnlyException() {
	super();
    }

    /**
     * Throw a IndexReadOnlyException with a message.
     */
    public IndexReadOnlyException(String s) {
	super(s);
    }
}
