// Position.java

package com.timeindexing.basic;

/**
 * A position in an index
 */
public interface Position extends Absolute, Cloneable {
    /**
     * Clone me
     */
    public Object clone() throws  CloneNotSupportedException;

    /**
     * Special End Of Index value.
     */
    public final static Position END_OF_INDEX = new Position() {
	    public long value() {
		return Long.MIN_VALUE;
	    }

	    public String toString() {
		return "END_OF_INDEX";
	    }
	    
	    public Object clone() throws  CloneNotSupportedException {
		return this;
	    }
	};

    /**
     * Special Too Low value.
     */
    public final static Position TOO_LOW = new Position() {
	    public long value() {
		return Long.MIN_VALUE;
	    }

	    public String toString() {
		return "TOO_LOW" + super.toString();
	    }
	    
	    public Object clone() throws  CloneNotSupportedException {
		return this;
	    }
	};

    /**
     * Special Too High value.
     */
    public final static Position TOO_HIGH = new Position() {
	    public long value() {
		return Long.MAX_VALUE;
	    }

	    public String toString() {
		return "TOO_HIGH" + super.toString();
	    }
	    
	    public Object clone() throws  CloneNotSupportedException {
		return this;
	    }
	};
}
