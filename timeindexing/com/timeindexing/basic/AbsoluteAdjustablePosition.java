// AbsoluteAdjustablePosition.java

package com.timeindexing.basic;

/**
 * An AbsoluteAdjustablePosition is a value that is an absolute position 
 * from the zeroth index element but can be modified.
 * This is basically a constant.
 */
public class AbsoluteAdjustablePosition implements AdjustablePosition, Absolute, Cloneable {
    /*
     * The position from 0;
     */
    long position = 0;

    /**
     * Construct a new AbsoluteAdjustablePosition
     */
    public AbsoluteAdjustablePosition() {
	position = 0;
    }

    /**
     * Construct a new AbsoluteAdjustablePosition from a given value
     */
    public AbsoluteAdjustablePosition(long value) {
	if (value >= 0) {
	    position = value;
	} else {
	    throw new IndexOutOfBoundsException("AbsoluteAdjustablePosition: value must be >= 0");
	}
    }

    /**
     * Construct a new AbsoluteAdjustablePosition from an exisitng Position
     */
    public AbsoluteAdjustablePosition(Position pos) {
	// get the value from the exisitng Position
	long value = pos.value();

	if (value >= 0) {
	    position = value;
	} else {
	    throw new IndexOutOfBoundsException("AbsoluteAdjustablePosition: Position value must be >= 0");
	}
    }

    /**
     * Get the count.
     */
    public long value() {
	return position;
    }

    /**
     * Adjust the position forwards or backward, given a value.
     */
    public Adjustable adjust(Value off) {
	return adjust(off.value());
    }

    /**
     * Adjust the position forwards or backward, given a basic value.
     */
    public Adjustable adjust(long amount) {
	if (position + amount >= 0) {
	    position += amount;
	    return this;
	} else {
	    throw new IndexOutOfBoundsException("AbsoluteAdjustablePosition value must be >= 0 after skipping. " + 
						position + " + " + amount + " < 0.");
	}
    }
     
    /**
     * Clone me
     */
    public Object clone() throws  CloneNotSupportedException {
	return super.clone();
    }


    /**
     * String value of a Position.
     */
    public String toString() {
	return "@"+position;
    }
    
}
