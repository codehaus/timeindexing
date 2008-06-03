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



// RelativeCount.java

package com.timeindexing.basic;

/**
 * An RelativeCount is a count that is an relative count from the current index element.
 * This is basically a constant, which can be +ve or -ve.
 */
public class RelativeCount implements Count, Relative, Cloneable {
    /*
     * The count from 0;
     */
    long count = 0;

    /**
     * Construct a new RelativeCount
     */
    RelativeCount() {
	count = 0;
    }

    /**
     * Construct a new RelativeCount from a given value
     */
    public RelativeCount(long value) {
	count = value;
    }

    /**
     * Construct a new RelativeCount from a Count
     */
    public RelativeCount(Count c) {
	this(c.value());
    }

    /**
     * Get the count.
     */
    public long value() {
	return count;
    }

    /**
     * Clone me
     */
    public Object clone() throws  CloneNotSupportedException {
	return super.clone();
    }
    
    /**
     * String value of a Count.
     */
    public String toString() {
	return ""+count;
    }
}
