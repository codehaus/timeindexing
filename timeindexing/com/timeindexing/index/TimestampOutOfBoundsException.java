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
// TimestampOutOfBoundsException.java

package com.timeindexing.index;

/**
 * An TimestampOutOfBoundsException is thrown when an attempt is made
 * to access a Timestamp that an Index does not have.
 */
public class TimestampOutOfBoundsException extends IndexOutOfBoundsException {
    /**
     * Throw a TimestampOutOfBoundsException with no message.
     */
    public TimestampOutOfBoundsException() {
	super();
    }

    /**
     * Throw a TimestampOutOfBoundsException with a message.
     */
    public TimestampOutOfBoundsException(String s) {
	super(s);
    }
}
