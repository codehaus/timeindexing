// TimestampOutOfBoundsException.java

package com.timeindexing.index;

/**
 * An TimestampOutOfBoundsException is thrown when an attempt is made
 * to access a Timestamp that an Index does not have.
 */
public class TimestampOutOfBoundsException extends IndexOutOfBoundsException {
    /**
     * Throw a TimestampOutOfBoundsException with no message.
     */
    public TimestampOutOfBoundsException() {
	super();
    }

    /**
     * Throw a TimestampOutOfBoundsException with a message.
     */
    public TimestampOutOfBoundsException(String s) {
	super(s);
    }
}
