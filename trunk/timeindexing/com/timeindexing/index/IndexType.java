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
     * A separate file based TimeIndex that writes no data only the idnex.
     */
    public final int SHADOW = 2;

    /**
     * An incore based TimeIndex, which has no storage capabilities.
     */
    public final int INCORE = 3;


}


