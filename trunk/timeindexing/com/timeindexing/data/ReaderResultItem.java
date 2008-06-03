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
// ReaderResultItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;
import com.timeindexing.plugin.ReaderResult;
import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of ByteBufferItem.
 */
public class ReaderResultItem implements DataItem {
    /*
     * The reader result
     */
    ReaderResult result = null;

    /**
     * Construct a ReaderResultItem from a ReaderResult.
     */
    public ReaderResultItem(ReaderResult rr) {
	result = rr;
    }

    /**
     * Get the data itself
     */
    public ByteBuffer getBytes() {
	return result.getData();
    }

    /**
     * Get the size of the item
     */
    public long getSize() {
	return getBytes().limit();
    }

    /**
     * Get the DataType of the DataItem.
     */
    public DataType getDataType() {
	return result.getDataType();
    }

    /**
     * Get the ByteBuffer object from this ReaderResultItem.
     * @return a ByteBuffer
     */
    public Object getObject() {
	return result;
    }

}
