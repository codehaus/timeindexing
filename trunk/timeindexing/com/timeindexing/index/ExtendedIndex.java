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



//ExtendedIndex.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.Offset;

/**
 * An interface for classes that need extended
 * Indexes.
 * It has the methods needed to access extra values of an index,
 * but are not generally needed by the application  layer.
 */
public interface ExtendedIndex extends Index, ExtendedIndexHeader {
    /**
     * Get the type of the index.
     */
    public IndexType getIndexType();

    /**
     * Get the  last time the index was flushed.
     */
    public Timestamp getLastFlushTime();

    /**
     * Get the IndexItem Position when the index was last flushed.
     */
    public Position getLastFlushPosition();
    
    /**
     * Get the Offset of the fisrt item.
     */
    public Offset getFirstOffset();

    /**
     * Get the Offset of the last item.
     */
    public Offset getLastOffset();

}
