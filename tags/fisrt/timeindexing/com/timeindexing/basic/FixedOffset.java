// FixedOffset.java

package com.timeindexing.basic;

/**
 * An FixedOffset is a value that is an absolute offset into a stream.
 * This is basically a constant.
 */
public class FixedOffset implements Position, Absolute, Cloneable {
    /*
     * The offset from 0;
     */
    long offset = 0;

    /**
     * Construct a new Fixedoffset
     */
    private FixedOffset() {
	offset = 0;
    }

    /**
     * Construct a new FixedOffset from a given value
     */
    public FixedOffset(long value) {
	if (value >= 0) {
	    offset = value;
	} else {
	    throw new IndexOutOfBoundsException("FixedOffset value must be >= 0");
	}
    }

    /**
     * Get the count.
     */
    public long value() {
	return offset;
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
