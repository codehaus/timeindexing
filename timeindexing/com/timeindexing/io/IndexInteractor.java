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



// IndexInteractor.java

package com.timeindexing.io;

import com.timeindexing.index.StoredIndex;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.basic.Position;

import java.io.IOException;

/**
 * An interface for objects that interact with indexes.
 */
public interface IndexInteractor { 
    /**
     * Get the index which this is doing I/O for.
     */
    public StoredIndex getIndex();

    /**
     * Get the item
     * @param position the position of the index item to get
     * @param withData read the data for this IndexItem if withData is true,
     * the data needs to be read at a later time, otherwise
     */
    public ManagedIndexItem getItem(Position position, boolean withData) throws IOException;


    /**
     * Add an IndexItem to the  index.
     */
    public long addItem(ManagedIndexItem item) throws IOException;

}
