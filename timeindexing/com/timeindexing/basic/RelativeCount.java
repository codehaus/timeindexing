// RelativeCount.java

package com.timeindexing.basic;

/**
 * An RelativeCount is a count that is an relative count from the current index element.
 * This is basically a constant, which can be +ve or -ve.
 */
public class RelativeCount implements Count, Relative, Cloneable {
    /*
     * The count from 0;
     */
    long count = 0;

    /**
     * Construct a new RelativeCount
     */
    RelativeCount() {
	count = 0;
    }

    /**
     * Construct a new RelativeCount from a given value
     */
    public RelativeCount(long value) {
	count = value;
    }

    /**
     * Construct a new RelativeCount from a Count
     */
    public RelativeCount(Count c) {
	this(c.value());
    }

    /**
     * Get the count.
     */
    public long value() {
	return count;
    }

    /**
     * Clone me
     */
    public Object clone() throws  CloneNotSupportedException {
	return super.clone();
    }
    
    /**
     * String value of a Count.
     */
    public String toString() {
	return ""+count;
    }
}
