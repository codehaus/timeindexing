// IndexType.java

package com.timeindexing.index;


/**
 * This is the generic definitions for different kinds of timeindex.
 */
public interface IndexType {
    /**
     * A inline based TimeIndex
     */
    public final int INLINE = 0;

    /**
     * A separate file based TimeIndex
     */
    public final int EXTERNAL = 1;

    /**
     * An incore based TimeIndex, which has no storage capabilities.
     */
    public final int INCORE = 2;

    /**
     * A Java serialization based TimeIndex
     */
    public final int JAVASERIAL = 3;

}


