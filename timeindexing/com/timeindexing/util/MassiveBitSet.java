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
// MassiveBitSet.java

package com.timeindexing.util;

import java.util.BitSet;

/**
 * A MassiveBitSet is used for appplications where a massive BitSet
 * is required. 
 * It is based on java.util.BitSet, but rather than
 * resizing and copying an existing BitSet it uses a DoubleLinkedList
 * of fixed size BitSets.
 */
public class MassiveBitSet {
    // The size of the underlyinmg BitSet
    static int SET_SIZE = 2048;

    // The doubly linked list of BitSets
    DoubleLinkedList linkedList = null;

    // the highest bit number to be set
    long highest = 0;

    /**
     * Construct a MassiveBitSet
     */
    public MassiveBitSet() {
	linkedList = new DoubleLinkedList();
	allocate(1);
    }

    /**
     * Construct a MassiveBitSet
     * @param n the no of bits to pre-allocate
     */
    public MassiveBitSet(long n) {
	linkedList = new DoubleLinkedList();
	allocate(n);
    }

    /**
     * Get the no of bits that can be currently stored
     * in this MassiveBitSet.
     */
    public long size() {
	return linkedList.size() * SET_SIZE;
    }

    /**
     * Get the value of the nth bit.
     * @param n the bit to get
     */
    public boolean get(long n) {
	if (n >= size()) {
	    return false;
	} else {
	    // get the appropriate BitSet
	    BitSet segment = (BitSet)linkedList.get(n / SET_SIZE);
	    // get the bit
	    boolean requiredBit = segment.get((int)(n % SET_SIZE));

	    return requiredBit;
	}
    }

    /**
     * Clear the value of the nth bit.
     * @param n the bit to get
     */
    public MassiveBitSet clear(long n) {
	if (n >= size()) {
	    return this;
	} else {
	    // get the appropriate BitSet
	    BitSet segment = (BitSet)linkedList.get((int)(n / SET_SIZE));
	    // clear the bit
	    segment.clear((int)(n % SET_SIZE));

	    return this;
	}
    }

    /**
     * Clear all of the MassiveBitSet.
     */
    public MassiveBitSet clear() {
	long listSize = linkedList.size();

	long element=0; 

	while (element < listSize) {
	    BitSet aBitSet = (BitSet)linkedList.get(element);

	    aBitSet.clear();
	    element++;
	}

	return this;
    }

    /**
     * This resets the MassiveBitSet by clearing all of the bits,
     * and setting the highest back to zero.
     */
    public MassiveBitSet reset() {
	linkedList.clear();
	allocate(1);
	highest = 0;

	return this;
    }

    /**
     * Set the value of the nth bit.
     * @param n the bit to get
     */
    public MassiveBitSet set(long n) {
	return set(n, true);
    }

    /**
     * Set the value of the nth bit.
     * @param n the bit to get
     * @param value the value to set bit n to
     */
    public MassiveBitSet set(long n, boolean value) {
	if (n >= size()) {  // this MassiveBitSet isn't big enough to set bit n
	    allocate(n);   // allocate enough space 
	}

	//System.err.println("Getting dll element " + (n / SET_SIZE) +  "/" + 
	//		   linkedList.size() + " for bit " + n);

	// get the appropriate BitSet
	BitSet segment = (BitSet)linkedList.get((int)(n / SET_SIZE));
	// set the bit
	segment.set((int)(n % SET_SIZE), value);

	// set the highest
	if (n > highest) {
	    highest = n;
	}

	return this;
    }

    /**
     * Return the highest bit position to be set.
     */
    public long highest() {
	return highest;
    }

    /**
     * Allocate enough space to hold up to n bits.
     */
    protected MassiveBitSet allocate(long n) {
	if (n >= size()) {   // n really is > the highest bit we can hold
	    // so work out how many BitSets we need
	    long need = (n / SET_SIZE) + 1;
	    long got = linkedList.size();

	    long alloc=got; 

	    //System.err.println("MassiveBitSet: allocate BitSet from " + got + " to " + need);

	    while (alloc < need) {
		linkedList.add(new BitSet(SET_SIZE));

		alloc++;

		//System.err.println("MassiveBitSet: allocated BitSet from " + got + " to " + alloc + " for bit " + n);

	    }

	    return this;
	} else {
	    // nothing to allocate
	    return this;
	}
    }
}
