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
// IndexHeaderWriter.java

package com.timeindexing.io;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Properties;

/**
 * An index header writer
 * This is the generic interface to an index writer.
 */
public interface IndexHeaderWriter {
    /**
     * Flush the current values to the header file.
     */
    public long flush() throws IOException;

    /**
     * Write the contents of the header file out
     * It assumes the header file is alreayd open for writing.
     */
    public long write() throws IOException;

    /**
     * Open an index header, given a filename.
     */
    public boolean open(String filename) throws IOException;

    /**
     * Create an index header, given a filename.
     */
    public boolean create(String filename) throws IOException;

    /**
     * Create an index header, given a filename
     * and some create time options.
     */
    public boolean create(String filename, Properties options) throws IOException;

    /**
     * Is the index header open
     */
    public boolean isOpen();

    /**
     * Close an index header reader.
     */
    public long close() throws IOException;

}
