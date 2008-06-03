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
// ReaderResult.java

package com.timeindexing.plugin;

import com.timeindexing.time.Timestamp;
import com.timeindexing.index.DataType;
import java.nio.ByteBuffer;

/**
 * An interface for the value returned by a reader plugin.
 */
public interface ReaderResult {
    /**
     * Get the data associated with this ReaderResult.
     */
    public ByteBuffer getData();

    /**
     * Get the data timestamp.
     * @return null if the data has no specific timestamp.
     */
    public Timestamp getDataTimestamp();


    /**
     * Get the DataType for this data
     */
    public DataType getDataType();
       

}
