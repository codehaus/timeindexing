// DataStyle.java

package com.timeindexing.index;

/**
 * Data style contants.
 */
public interface DataStyle {
    /**
     * Data is kept incore with the index.
     */
    public final int INCORE = 0;

    /**
     * Data is kept inline in the index.
     */
    public final int INLINE = 1;

    /**
     * Data is kept external to the index.
     */
    public final int EXTERNAL = 2;
}
