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



// UID.java

package com.timeindexing.basic;

import java.io.Serializable;

/**
 * This represents a Unique ID.
 * The constructor generates an ID which cannot be changed.
 */

public class UID extends AID implements ID, Serializable {
    /**
     * An ID generator.
     */
    private synchronized static long generate(UID forWhom) {
	// 1 day = 86400000 u Sec
	// allow 42 bits for time sunce 01/01/1970
	// this is enough for 50903 days  =~ 139 years
	// so give 22 bits for other data
	return   + (((long)forWhom.superHash() << 42) >>> 42) | (System.currentTimeMillis() <<22);
    }

    /**
     * Create a new ID object.
     * This will be initialised with a unique ID.
     */
    public UID() {
	idValue = UID.generate(this);
    }

    /**
     * Determine what the hash code would have been if
     * it had not been over ridden here.
     */
    /**
     * Determine what the hash code would have been if
     * it had not been over ridden here.
     */
    private int superHash() {
	return System.identityHashCode(this);
    }

    /*
      private int superHash() {
      return super.hashCode();
      }
    */

}


