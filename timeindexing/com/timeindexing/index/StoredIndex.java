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
// StoredIndex.java

package com.timeindexing.index;

/**
 * An interface for classes that need to process
 * Indexes that are saved in stores.
 * This is to be used by classes that store index
 * data, rather than have data incore.
 */
public interface StoredIndex extends ManagedIndex  {
    /**
     * Retrieve an Index Item into the Index.
     * This is a callback for the IOInteractor.
     * @param item the IndexItem to add
     * @param position the position to add the item at
     * @return the no of items in the cache
     */
    public long retrieveItem(IndexItem item, long position);

    /**
     * Read data for an index item
     * given an index position and a DataReference.
     */
    public DataHolderObject readData(long pos, DataReference dataReference);
}
