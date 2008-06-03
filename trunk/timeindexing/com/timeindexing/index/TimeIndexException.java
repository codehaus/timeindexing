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
// TimeIndexException.java

package com.timeindexing.index;

/**
 * A TimeIndexException is a generic base exception
 * for all Exceptions in the TimeIndex system.
 */
public class TimeIndexException extends Exception {
    /**
     * Throw a TimeIndexException with no message.
     */
    public TimeIndexException() {
	super();
    }

    /**
     * Throw a TimeIndexException with a message.
     */
    public TimeIndexException(String s) {
	super(s);
    }

    /**
     * Throw a TimeIndexException with an exception
     */
    public TimeIndexException(Throwable t) {
	super(t);
    }
}
