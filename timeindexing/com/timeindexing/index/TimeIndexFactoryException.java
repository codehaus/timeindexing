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
// TimeIndexFactoryException.java

package com.timeindexing.index;

/**
 * A TimeIndexFactoryException is thrown when the TimeIndexFactory 
 * cannot resolve a value.
 */
public class TimeIndexFactoryException extends TimeIndexException {
    /**
     * Throw a TimeIndexFactoryException with no message.
     */
    public TimeIndexFactoryException() {
	super();
    }

    /**
     * Throw a TimeIndexFactoryException with a message.
     */
    public TimeIndexFactoryException(String s) {
	super(s);
    }
}
