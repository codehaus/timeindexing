// Count.java

package com.timeindexing.basic;

/**
 * An RelativeCount is a count that is an relative count from the current index element.
 * This is basically a constant, which can be +ve or -ve and can also be modified.
 */
public class RelativeAdjustableCount implements AdjustableCount, Relative, Cloneable {
    /*
     * The count from 0;
     */
    long count = 0;

    /**
     * Construct a new RelativeCount
     */
    public RelativeAdjustableCount() {
	count = 0;
    }

    /**
     * Construct a new RelativeCount from a given value
     */
    public RelativeAdjustableCount(long value) {
	count = value;
    }

    /**
     * Construct a new RelativeCount from an existing Count
     */
    public RelativeAdjustableCount(Count c) {
	// get the value from the exisitng Count
	long value = c.value();

	count = value;
    }

    /**
     * Get the count.
     */
    public long value() {
	return count;
    }

    /**
     * Adjust the coubnt forwards or backward, given a Value
     */
    public Adjustable adjust(Value off) {
	return adjust(off.value());
    }

    /**
     * Adjust the count forwards or backward, given a basic value.
     */
    public Adjustable adjust(long amount) {
	count += amount;
	return this;
    }
     
}
