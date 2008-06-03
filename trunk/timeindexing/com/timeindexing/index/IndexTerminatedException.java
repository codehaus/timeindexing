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



// IndexTerminatedException.java

package com.timeindexing.index;

/**
 * An IndexTerminatedException is thrown when an attempt is made
 * to add data to a terminated index.
 */
public class IndexTerminatedException extends TimeIndexException {
    /**
     * Throw a IndexTerminatedException with no message.
     */
    public IndexTerminatedException() {
	super();
    }

    /**
     * Throw a IndexTerminatedException with a message.
     */
    public IndexTerminatedException(String s) {
	super(s);
    }
}
