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
//ManagedIndexItem.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.AbsolutePosition;

/**
 * An interface for classes that need to manage IndexItems.
 * It has the methods needed to manage an item in an index,
 * but are not needed by the application  layer.
 */
public interface ManagedIndexItem extends IndexItem {
    /**
     * Set the Index for an IndexItem
     */
    public ManagedIndexItem setIndex(Index index);

    /**
     * Set the position for the index item in an index.
     */
    public ManagedIndexItem setPosition(AbsolutePosition position);

    /**
     * Set the last access time of the item.
     */
    public ManagedIndexItem setLastAccessTime();

    /**
     * Get the DataAbstraction held by the IndexItem.
     */
    public DataAbstraction getDataAbstraction();

}
