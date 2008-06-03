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



// IndexCreateException.java

package com.timeindexing.index;

/**
 * A IndexCreateException is thrown when an Index
 * cannot be created.
 * This usually because the Properties passed at create time
 * do not have enough information.
 */
public class IndexCreateException extends TimeIndexException {
    /**
     * Throw a IndexCreateException with no message.
     */
    public IndexCreateException() {
	super();
    }

    /**
     * Throw a IndexCreateException with a message.
     */
    public IndexCreateException(String s) {
	super(s);
    }

    /**
     * Throw a IndexCreateException with an  exception from the underlying cause.
     */
    public IndexCreateException(Exception e) {
	super(e);
    }

}
