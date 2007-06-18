// IndexType.java

package com.timeindexing.index;


/**
 * This is the generic definitions for different kinds of timeindex.
 */
public interface IndexType {
    /**
     * The value for this instance of the enumeration.
     */
    public int value();

    /**
     * A inline based TimeIndex
     */
    public final int INLINE_VALUE = 0;
    public final static IndexType INLINE = new IndexType() {
	 public int value() {
		return INLINE_VALUE;
	    }


	    public String toString() {
		return "INLINE";
	    }
	};   

    /**
     * A separate file based TimeIndex
     */
    public final int EXTERNAL_VALUE = 1;
    public final static IndexType EXTERNAL = new IndexType() {
	 public int value() {
		return EXTERNAL_VALUE;
	    }


	    public String toString() {
		return "EXTERNAL";
	    }
	};   


    /**
     * A separate file based TimeIndex that writes no data only the idnex.
     */
    public final int SHADOW_VALUE = 2;
    public final static IndexType SHADOW = new IndexType() {
	 public int value() {
		return SHADOW_VALUE;
	    }


	    public String toString() {
		return "SHADOW";
	    }
	};   


    /**
     * An incore based TimeIndex, which has no storage capabilities.
     */
    public final int INCORE_VALUE = 3;
    public final static IndexType INCORE = new IndexType() {
	 public int value() {
		return INCORE_VALUE;
	    }


	    public String toString() {
		return "INCORE";
	    }
	};   



}


