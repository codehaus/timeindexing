// Offset.java

package com.timeindexing.basic;

/**
 * An Offset is a value that is an absolute offset into a stream.
 * This is basically a constant.
 */
public class Offset implements Position, Absolute, Cloneable {
    /*
     * The offset from 0;
     */
    long offset = 0;

    /**
     * Construct a new offset
     */
    private Offset() {
	offset = 0;
    }

    /**
     * Construct a new Offset from a given value
     */
    public Offset(long value) {
	if (value >= 0) {
	    offset = value;
	} else {
	    throw new IndexOutOfBoundsException("Offset value must be >= 0");
	}
    }

    /**
     * Get the count.
     */
    public long value() {
	return offset;
    }

    /**
     * Get the position.
     */
    public Position position() {
	return this;
    }

    /**
     * Clone me
     */
    public Object clone() throws  CloneNotSupportedException {
	return super.clone();
    }


    /**
     * String value of a Offset.
     */
    public String toString() {
	return "@"+offset;
    }
}
