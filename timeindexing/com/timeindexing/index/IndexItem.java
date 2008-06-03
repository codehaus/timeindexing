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
//IndexItem.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.Size;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.time.Timestamp;
import com.timeindexing.data.DataItem;

import java.nio.ByteBuffer;

/**
 * Values that an index item must return.
 */
public interface IndexItem {
    /**
     * The timestamp in the data of the current IndexItem.
     * The Data timestamp is the same as the Sender timestamp.
     */
    public Timestamp getDataTimestamp();

    /**
     * The timestamp of the current IndexItem.
     * The Index timestamp is the same as the Receiver timestamp.
     */
    public Timestamp getIndexTimestamp();

    /**
     * A reference to the Data being indexed.
     */
    public ByteBuffer getData();

    /**
     * The size of the data item being referenced.
     */
    public Size getDataSize();

    /**
     * The type of the data item being referenced.
     */
    public DataType getDataType();

    /**
     * Get the data of this IndexItem as a DataItem.
     */
    public DataItem getDataItem();

    /**
     * The index ID.
     */
    public ID getItemID();

    /**
     * The meta data of annotations associated with this IndexItem.
     * The application is free to process this in any way.
     */
    public long getAnnotationMetaData();

    /**
     * Get the index position this IndexItem is in.
     */
    public AbsolutePosition getPosition();

    /**
     * Get the index this IndexItem is in.
     */
    public Index getIndex();

    /**
     * Get the last time this object was accessed.
     */
    public Timestamp getLastAccessTime();

    /** 
     * Is the data held by the IndexItem, actually an IndexReference.
     */
    public boolean isReference();

    /**
     * Follow the reference, if this IndexItem holds an IndexReference.
     */
    public IndexItem follow() throws GetItemException, IndexClosedException;
}
