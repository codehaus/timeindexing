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



// IndexCommitException.java

package com.timeindexing.index;

/**
 * An IndexCommitException is thrown when doing a commit()
 * on an Index fails.
 */
public class IndexCommitException extends TimeIndexException {
    /**
     * Throw a IndexCommitException with no message.
     */
    public IndexCommitException() {
	super();
    }

    /**
     * Throw a IndexCommitException with a message.
     */
    public IndexCommitException(String s) {
	super(s);
    }
}
