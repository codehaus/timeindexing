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
// DataItem.java

package  com.timeindexing.data;

import com.timeindexing.index.DataType;

import java.nio.ByteBuffer;

/**
 * An item of data presented by a data reader into the index.
 * This is the generic interface to the data coming into the system.
 */
public interface DataItem {
    /**
     * Get the data itself
     */
    public ByteBuffer getBytes();

    /**
     * Get the size of the data item
     */
    public long getSize();

    /**
     * Get the DataType of the DataItem.
     */
    public DataType getDataType();

    /**
     * Get the object from a DataItem.
     */
    public Object getObject();

}
