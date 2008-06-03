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



// AddItemException.java

package com.timeindexing.index;

/**
 * An AddItemException is thrown when adding an item
 * to an index fails.
 */
public class AddItemException extends IndexItemException {
    /**
     * Throw a AddItemException with no message.
     */
    public AddItemException() {
	super();
    }

    /**
     * Throw a AddItemException with a message.
     */
    public AddItemException(String s) {
	super(s);
    }

    /**
     * Throw a AddItemException with an  exception from the underlying cause.
     */
    public AddItemException(Exception e) {
	super(e);
    }

}
