// IllegalIntervalValue.jav

package com.timeindexing.basic;


/**
 * An IllegalIntervalValue is thrown when an a null is passed 
 * as argument to an Interval constructor.
 */
public class IllegalIntervalValue extends IndexOutOfBoundsException {
    /**
     * Throw a IllegalIntervalValue with no message.
     */
    public IllegalIntervalValue() {
	super();
    }

    /**
     * Throw a IllegalIntervalValue with a message.
     */
    public IllegalIntervalValue(String s) {
	super(s);
    }
}
