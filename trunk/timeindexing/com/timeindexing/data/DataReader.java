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
// DataReader.java

package com.timeindexing.data;

import java.io.IOException;

/**
 * A data reader gets data from the application.
 * This is the generic interface to an data reader.
 */
public interface DataReader {
    /**
     * Read an item of data from the input
     */
    public DataItem read() throws IOException;

    /**
     * Close an data reader.
     */
    public boolean close() throws IOException;

    /**
     * Is the reader ready to present more data.
     */
    public boolean ready() throws IOException;
}
