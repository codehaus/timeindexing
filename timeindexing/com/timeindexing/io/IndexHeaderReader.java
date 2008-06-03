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
// IndexHeaderReader.java

package com.timeindexing.io;

import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * An index header reader
 * This is the generic interface to an index reader.
 */
public interface IndexHeaderReader {
    /**
     * Open an index header
     */
    public boolean open(String filename) throws IOException;

    /**
     * Is the index header open
     */
    public boolean isOpen();

    /**
     * Read an index header from the header stream.
     */
    public long read() throws IOException;

    /**
     * Close an index header reader.
     */
    public long close() throws IOException;
}
