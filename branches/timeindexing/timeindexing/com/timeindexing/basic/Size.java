// Size.java

package com.timeindexing.basic;

/**
 * An Size is an absolute value.
 * This is basically a constant.
 */
public class Size implements  Absolute, Cloneable  {
    /*
     * The size from 0;
     */
    long size = 0;

    /**
     * Construct a new Size
     */
    private Size() {
    }

    /**
     * Construct a new Size from a given value
     */
    public Size(long value) {
	if (value >= 0) {
	    size = value;
	} else {
	    throw new IndexOutOfBoundsException("Size value must be >= 0");
	}
    }


    /**
     * Get the count.
     */
    public long value() {
	return size;
    }

    /**
     * Clone me
     */
    public Object clone() throws  CloneNotSupportedException {
	return super.clone();
    }


    /**
     * String value of a Size.
     */
    public String toString() {
	return "["+size+"]";
    }
}
