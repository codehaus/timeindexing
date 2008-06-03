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
// NanosecondScale.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;

/**
 * A time that is in the Nanosecond Scale.
 */
public interface NanosecondScale extends Scale {
    public NanosecondScale SCALE = new NanosecondScale() {
	    /**
	     * Scale is 10 ^ -9
	     */
	    public long value() {
		return 1000000000;
	    }

	    public String toString() {
		return "Nanosecond Scale";
	    }

	};
}
