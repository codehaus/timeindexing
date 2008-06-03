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
// Overlap.java

package com.timeindexing.basic;

/**
 * An enumeration of 2 values to enable choice between 
 * having 2 ways for an Interval to overlap an index.
 * Strict requires that the Interval must be within the
 * bounds of the Index.
 * Free allows either end of the Interval to go beyond the bounds
 * of an Index.
 */
public interface Overlap {
    /**
     * States if the Interval must be strictly in the bounds of an Index.
     */
    public final static Overlap STRICT = new Overlap() {
	    
	    public String toString() {
		return "STRICT";
	    }
	};

    /**
     * States if the Interval can be free of the bounds of an Index.
     */
    public final static Overlap FREE = new Overlap() {
	    
	    public String toString() {
		return "FREE";
	    }
	};
}
