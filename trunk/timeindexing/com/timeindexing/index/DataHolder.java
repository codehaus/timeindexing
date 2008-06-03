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



// DataHolder.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;

import java.nio.ByteBuffer;

/**
 * An interface for objects that act as holders
 * of data from an index.
 */
public interface DataHolder extends DataAbstraction {
    /**
     * Get the bytes.
     */
    public ByteBuffer getBytes();

    /**
     * Get the last time this object was accessed.
     */
    public Timestamp getLastAccessTime();

    /**
     * Get the time the data was read from storage into this object.
     */
    public Timestamp getReadTime();
}
