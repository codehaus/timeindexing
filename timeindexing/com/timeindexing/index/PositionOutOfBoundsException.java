// PositionOutOfBoundsException.java

package com.timeindexing.index;

/**
 * An PositionOutOfBoundsException is thrown when an attempt is made
 * to access a Position that an Index does not have.
 */
public class PositionOutOfBoundsException extends IndexOutOfBoundsException {
    /**
     * Throw a PositionOutOfBoundsException with no message.
     */
    public PositionOutOfBoundsException() {
	super();
    }

    /**
     * Throw a PositionOutOfBoundsException with a message.
     */
    public PositionOutOfBoundsException(String s) {
	super(s);
    }
}
