// Function.java

package com.timeindexing.index;

/**
 * An interface for functions that are passed to filter() and map().
 * <p>
 * The identity function can be coded as:
 * <tt>
 * Function identity = new Function() {
 *     public Object evaluate(IndexItem i) {
 *         return i;
 *     }
 * }
 * </tt>
 */
public interface Function {
    /**
     * Evaluate an IndexItem in some way.
     * @return an Object, if the function evaluates, null otherwise
     */
    public Object evaluate(IndexItem i);
}
