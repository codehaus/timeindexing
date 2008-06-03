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



// Lifetime.java

package com.timeindexing.time;


/**
 * An enumeration of 2 values to enable choice between
 * timestamps whose lifetime is continuous, and those which are discrete.
 * Continuous timestamps have a lifetime which starts at the time of the
 * timestamp and ends when a new timestamp is put into an index.
 * Discrete timestamps exist only at the point in time of the time of the
 * timestamp.
 */
public interface Lifetime {
    /**
     * A selector for timestamps with a Continuous lifetime.
     */
    public static final Lifetime CONTINUOUS = new Lifetime() {
	    
	    public String toString() {
		return "CONTINUOUS";
	    }
	};

    /**
     * A selector for timestamps with a Discrete lifetime.
     */
    public static final Lifetime DISCRETE = new Lifetime() {
	    public String toString() {
		return "DISCRETE";
	    }
	};
}
