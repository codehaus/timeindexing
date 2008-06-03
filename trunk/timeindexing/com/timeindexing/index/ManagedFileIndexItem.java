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
//ManagedFileIndexItem.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.Offset;

/**
 * An interface for classes that need to manage
 * IndexItems that reside in an Index file.
 * It has the methods needed to manage an item from a file,
 * but are not needed by the application  layer.
 */
public interface ManagedFileIndexItem extends ManagedIndexItem {
    /**
     * Does this IndexItem actually hold the data.
     */
    public boolean hasData();

    /**
     * Set the data to be a new DataAbstraction.
     */
    public ManagedFileIndexItem setData(DataAbstraction data);

    /**
     * Get the file offset for the index for this index item.
     */
    public Offset getIndexOffset();

    /**
     * Set the file offset for the index item for this index item.
     */
    public ManagedFileIndexItem setIndexOffset(Offset offset);

    /**
     * Get the file offset for the data for this index item.
     */
    public Offset getDataOffset();

    /**
     * Set the file offset for the data for this index item.
     */
    public ManagedFileIndexItem setDataOffset(Offset offset);

}
