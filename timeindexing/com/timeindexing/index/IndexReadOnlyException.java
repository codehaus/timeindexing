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
// IndexReadOnlyException.java

package com.timeindexing.index;

/**
 * An IndexReadOnlyException is thrown when an attempt is made
 * to activate an Index that is read-only.
 */
public class IndexReadOnlyException extends TimeIndexException {
    /**
     * Throw a IndexReadOnlyException with no message.
     */
    public IndexReadOnlyException() {
	super();
    }

    /**
     * Throw a IndexReadOnlyException with a message.
     */
    public IndexReadOnlyException(String s) {
	super(s);
    }
}
