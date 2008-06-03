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



// DataCache.java

package com.timeindexing.cache;

import com.timeindexing.index.DataAbstraction;

/**
 * The data objects referenced by the index.
 * The data may or may not be in core.
 * It is up to the implementations to decide if a data object
 * will be kept in core.
 */
public interface DataCache {
    /**
     * Hold a Data Object.
     */
    public int addData(DataAbstraction data);

    /**
     * Flush this data cache.
     */
    public boolean flush();

    /**
     * Close this data cache.
     */
    public boolean close();

}
