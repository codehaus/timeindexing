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
    public final int INLINE = 0;
    public final static IndexType INLINE_DT = new IndexType() {
	 public int value() {
		return INLINE;
	    }


	    public String toString() {
		return "INLINE";
	    }
	};   

    /**
     * A separate file based TimeIndex
     */
    public final int EXTERNAL = 1;
    public final static IndexType EXTERNAL_DT = new IndexType() {
	 public int value() {
		return EXTERNAL;
	    }


	    public String toString() {
		return "EXTERNAL";
	    }
	};   


    /**
     * A separate file based TimeIndex that writes no data only the idnex.
     */
    public final int SHADOW = 2;
    public final static IndexType SHADOW_DT = new IndexType() {
	 public int value() {
		return SHADOW;
	    }


	    public String toString() {
		return "SHADOW";
	    }
	};   


    /**
     * An incore based TimeIndex, which has no storage capabilities.
     */
    public final int INCORE = 3;
    public final static IndexType INCORE_DT = new IndexType() {
	 public int value() {
		return INCORE;
	    }


	    public String toString() {
		return "INCORE";
	    }
	};   



}


