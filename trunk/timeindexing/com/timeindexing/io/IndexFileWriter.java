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
// IndexFileWriter.java

package com.timeindexing.io;

import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.IndexOpenException;
import com.timeindexing.index.IndexCreateException;

import java.io.IOException;
import java.nio.channels.FileLock;

/**
 * An interface for writers of indexes.
 */
public interface IndexFileWriter { 
    public long open(IndexProperties indexProperties) throws IOException, IndexOpenException;

    /**
     * create an Index
     */
    public long create(IndexProperties indexProperties) throws IOException, IndexCreateException;

    /**
     * Write the header to the  index.
     * @param headerType the type of header, e.g FileType.INLINE_INDEX or FileType.EXTERNAL_INDEX
     */
    public long writeHeader(byte headerType) throws IOException;

    /**
     * Write an IndexItem to the  index.
     */
    public long writeItem(ManagedIndexItem item) throws IOException;

    /**
     * Get the append position
     */
    public long getAppendPosition();

    /**
     * Goto the append position
     */
    public boolean gotoAppendPosition() throws IOException;

    /**
     * Flush the  index.
     */
    public long flush() throws IOException;
    /**
     * Close the  index.
     */
    public long close() throws IOException;

    /**
     * Get a write-lock on this index.
     */
    public FileLock getWriteLock();


    /**
     * Release a FileLock.
     */
    public boolean releaseWriteLock();

    /**
     * Has the Index been write-locked.
     */
    public boolean isWriteLocked();
}
