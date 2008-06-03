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
// PositionParser.java

package com.timeindexing.appl;

import com.timeindexing.basic.Position;
import com.timeindexing.basic.AbsolutePosition;

/**
 * Parses a position from a given input.
 */
public class PositionParser {
    /**
     * Construct a PositionParser.
     */
    public PositionParser() {
    }

    /**
     * Parse a position given as a String.
     * e.g "500"
     * @return a Position object if the input is valid, null otherwise.
     */
    public AbsolutePosition parse(String posStr) {
	long result = 0;

	try {
	    result = Long.parseLong(posStr);
	    return new AbsolutePosition(result);
	} catch (NumberFormatException nfe) {
	    return null;
	}
    }
}

