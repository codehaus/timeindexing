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



// Position.java

package com.timeindexing.basic;

/**
 * A position in an index
 */
public interface Position extends Absolute, Cloneable {
    /**
     * Clone me
     */
    public Object clone() throws  CloneNotSupportedException;

    /**
     * Get the position.
     */
    public Position position();

    /**
     * Special End Of Index value.
     */
    public final static Position END_OF_INDEX = new Position() {
	    public long value() {
		return Long.MIN_VALUE;
	    }

	    public String toString() {
		return "END_OF_INDEX";
	    }
	    
	    public Position position() {
		return this;
	    }

	    public Object clone() throws  CloneNotSupportedException {
		return this;
	    }
	};

    /**
     * Special Too Low value.
     */
    public final static Position TOO_LOW = new Position() {
	    public long value() {
		return Long.MIN_VALUE;
	    }

	    public String toString() {
		return "TOO_LOW";
	    }
	    	    
	    public Position position() {
		return this;
	    }

	    public Object clone() throws  CloneNotSupportedException {
		return this;
	    }
	};

    /**
     * Special Too High value.
     */
    public final static Position TOO_HIGH = new Position() {
	    public long value() {
		return Long.MAX_VALUE;
	    }

	    public String toString() {
		return "TOO_HIGH";
	    }
	    
	    public Position position() {
		return this;
	    }

	    public Object clone() throws  CloneNotSupportedException {
		return this;
	    }
	};
}
