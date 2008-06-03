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



// Count.java

package com.timeindexing.basic;

/**
 * An RelativeCount is a count that is an relative count from the current index element.
 * This is basically a constant, which can be +ve or -ve and can also be modified.
 */
public class RelativeAdjustableCount extends RelativeCount implements AdjustableCount, Relative, Cloneable {
    /**
     * Construct a new RelativeCount
     */
    public RelativeAdjustableCount() {
	count = 0;
    }

    /**
     * Construct a new RelativeCount from a given value
     */
    public RelativeAdjustableCount(long value) {
	count = value;
    }

    /**
     * Construct a new RelativeCount from an existing Count
     */
    public RelativeAdjustableCount(Count c) {
	// get the value from the exisitng Count
	long value = c.value();

	count = value;
    }

    /**
     * Adjust the coubnt forwards or backward, given a Value
     */
    public Adjustable adjust(Value off) {
	return adjust(off.value());
    }

    /**
     * Adjust the count forwards or backward, given a basic value.
     */
    public Adjustable adjust(long amount) {
	count += amount;
	return this;
    }
     
}
