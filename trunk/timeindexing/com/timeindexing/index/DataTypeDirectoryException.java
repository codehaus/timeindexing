// DataTypeDirectoryException.java

package com.timeindexing.index;

/**
 * A DataTypeDirectoryException is thrown when the DataTypeDirectory 
 * cannot resolve a value.
 */
public class DataTypeDirectoryException extends RuntimeException {
    /**
     * Throw a DataTypeDirectoryException with no message.
     */
    public DataTypeDirectoryException() {
	super();
    }

    /**
     * Throw a DataTypeDirectoryException with a message.
     */
    public DataTypeDirectoryException(String s) {
	super(s);
    }
}
