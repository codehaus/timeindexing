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
// GetItemException.java

package com.timeindexing.index;

/**
 * An GetItemException is thrown when getting an item
 * from an index fails.
 */
public class GetItemException extends IndexItemException {
    /**
     * Throw a GetItemException with no message.
     */
    public GetItemException() {
	super();
    }

    /**
     * Throw a GetItemException with a message.
     */
    public GetItemException(String s) {
	super(s);
    }

    /**
     * Throw a GetItemException with an  exception from the underlying cause.
     */
    public GetItemException(Exception e) {
	super(e);
    }

}
