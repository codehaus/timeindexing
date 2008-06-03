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



// IllegalIntervalValue.jav

package com.timeindexing.basic;


/**
 * An IllegalIntervalValue is thrown when an a null is passed 
 * as argument to an Interval constructor.
 */
public class IllegalIntervalValue extends IndexOutOfBoundsException {
    /**
     * Throw a IllegalIntervalValue with no message.
     */
    public IllegalIntervalValue() {
	super();
    }

    /**
     * Throw a IllegalIntervalValue with a message.
     */
    public IllegalIntervalValue(String s) {
	super(s);
    }
}
