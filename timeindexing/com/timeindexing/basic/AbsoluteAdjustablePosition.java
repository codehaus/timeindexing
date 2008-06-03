/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



// AbsoluteAdjustablePosition.java

package com.timeindexing.basic;

/**
 * An AbsoluteAdjustablePosition is a value that is an absolute position 
 * from the zeroth index element but can be modified.
 */
public class AbsoluteAdjustablePosition extends AbsolutePosition implements AdjustablePosition, Absolute, Cloneable {
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
     * Adjust the position forwards or backward, given a value.
     */
    public Adjustable adjust(Value off) {
	return adjust(off.value());
    }

    /**
     * Adjust the position forwards or backward, given a basic value.
     * Adjusting below 0, sets the position to 0;
     */
    public Adjustable adjust(long amount) {
	if (position + amount >= 0) {
	    position += amount;
	    return this;
	} else {
	    //throw new IndexOutOfBoundsException("AbsoluteAdjustablePosition value must be >= 0 after skipping. " + position + " + " + amount + " < 0.");
	    position = 0;
	    return this;
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
