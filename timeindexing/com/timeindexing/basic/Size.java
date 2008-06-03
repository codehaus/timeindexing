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



// Size.java

package com.timeindexing.basic;

/**
 * An Size is an absolute value.
 * This is basically a constant.
 */
public class Size implements  Absolute, Cloneable  {
    /*
     * The size from 0;
     */
    long size = 0;

    /**
     * Construct a new Size
     */
    private Size() {
    }

    /**
     * Construct a new Size from a given value
     */
    public Size(long value) {
	if (value >= 0) {
	    size = value;
	} else {
	    throw new IndexOutOfBoundsException("Size value must be >= 0");
	}
    }


    /**
     * Get the count.
     */
    public long value() {
	return size;
    }

    /**
     * Clone me
     */
    public Object clone() throws  CloneNotSupportedException {
	return super.clone();
    }


    /**
     * String value of a Size.
     */
    public String toString() {
	return "["+size+"]";
    }
}
