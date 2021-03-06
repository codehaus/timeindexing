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



// IndexFlushException.java

package com.timeindexing.index;

/**
 * An IndexFlushException is thrown when doing a flush()
 * on an Index fails.
 */
public class IndexFlushException extends TimeIndexException {
    /**
     * Throw a IndexFlushException with no message.
     */
    public IndexFlushException() {
	super();
    }

    /**
     * Throw a IndexFlushException with a message.
     */
    public IndexFlushException(String s) {
	super(s);
    }
}
