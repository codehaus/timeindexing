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



// IndexTypeException.java

package com.timeindexing.index;

/**
 * A IndexTypeException is thrown when an Index
 * cannot be typed.
 * This usually because the file is not actually an index file.
 */
public class IndexTypeException extends TimeIndexException {
    /**
     * Throw a IndexTypeException with no message.
     */
    public IndexTypeException() {
	super();
    }

    /**
     * Throw a IndexTypeException with a message.
     */
    public IndexTypeException(String s) {
	super(s);
    }
}
