// TimestampDecoderException.java

package com.timeindexing.time;

/**
 * A TimestampDecoderException is thrown when a TimestampDecoder cannot
 * decode a value posing as a time.
 */
public class TimestampDecoderException extends RuntimeException {
    /**
     * Throw a TimestampDecoderException with no message.
     */
    public TimestampDecoderException() {
	super();
    }

    /**
     * Throw a TimestampDecoderException with a message.
     */
    public TimestampDecoderException(String s) {
	super(s);
    }
}
