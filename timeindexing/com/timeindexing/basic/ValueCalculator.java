// ValueCalculator.java

package com.timeindexing.basic;

/**
 * Do some basic calculations on Values.
 * @see com.timeindexing.time.TimeCalculator
 */
public class ValueCalculator  {
    /*
     * Ordinals: ==, != , <, <=, >, >=
     */

    /**
     * Equals
     */
    public static boolean equals(Value v1, Value v2) {
	return v1.value() == v2.value();
    }

    /**
     * Not Equals
     */
    public static boolean notEquals(Value v1, Value v2) {
	return ! equals(v1, v2);
    }

    /**
     * LessThanEquals
     */
    public static boolean lessThanEquals(Value v1, Value v2) {
	return v1.value() <= v2.value();
    }

    /**
     * LessThan
     */
    public static boolean lessThan(Value v1, Value v2) {
	return  ! greaterThanEquals(v1, v2);
    }

    /**
     * GreaterThanEquals
     */
    public static boolean greaterThanEquals(Value v1, Value v2) {
	return v1.value() >= v2.value();
    }

    /**
     * Greater Than
     */
    public static boolean greaterThan(Value v1, Value v2) {
	return  ! lessThanEquals(v1, v2);
    }

}
