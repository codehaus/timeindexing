Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
// AbsolutePosition.java

package com.timeindexing.basic;

/**
 * An AbsolutePosition is a value that is an absolute position from the zeroth index element.
 * This is basically a constant.
 */
public class AbsolutePosition implements Position, Absolute, Cloneable {
    /*
     * The position from 0;
     */
    long position = 0;

    /**
     * Construct a new AbsolutePosition
     */
    AbsolutePosition() {
	position = 0;
    }

    /**
     * Construct a new AbsolutePosition from a given value
     */
    public AbsolutePosition(long value) {
	if (value >= 0) {
	    position = value;
	} else {
	    throw new IndexOutOfBoundsException("AbsolutePosition value must be >= 0");
	}
    }

    /**
     * Construct a new AbsolutePosition from a Position
     */
    public AbsolutePosition(Position pos) {
	this(pos.value());
    }

    /**
     * Get the position.
     */
    public Position position() {
	return this;
    }

    /**
     * Get the count.
     */
    public long value() {
	return position;
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
