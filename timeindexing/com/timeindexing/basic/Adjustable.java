// Adjustable.java

package com.timeindexing.basic;

/**
 * An adjustable value
 */
public interface Adjustable extends Value {
    /**
     * Adjust the Value by skipping around,
     * forwards or backwards.
     */
    public Adjustable adjust(Value off);

    /**
     * Adjust the Value by skiinog around,
     * forwards or backwards.
     */
    public Adjustable adjust(long amount);

}
