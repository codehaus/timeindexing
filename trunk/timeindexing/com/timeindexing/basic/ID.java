// ID.java

package com.timeindexing.basic;

/**
 * An interface for an ID.
 * An ID has to be able to return a long version of itself.
 */
public interface ID {
    /**
     * Return a long form of an ID, if poosible.
     */
    public long value();
}
