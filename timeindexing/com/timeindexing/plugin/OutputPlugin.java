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
// OutputPlugin.java

package com.timeindexing.plugin;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexProperties;
import java.io.OutputStream;
import java.io.IOException;

/**
 * An interface for output plugins.
 */
public interface OutputPlugin {
    /**
     * Get the index we are doing output for.
     */
    public Index getIndex();

    /**
     * Get the OutputStream for the OutputPlugin.
     */
    public OutputStream getOutputStream();

    /**
     * Set the context for the OutputPlugin, which is
     * the Index we are going output for, and the OutputStream
     * that is being written to.
     */
    public OutputPlugin setContext(Index index, OutputStream outStream);

    /**
     * Set a writer plugin, to read input from the InputStream.
     */
    public OutputPlugin setWriterPlugin(WriterPlugin writer);

    /**
     * Get the writer plugin.
     */
    public WriterPlugin getWriterPlugin();
    /**
     * Do some output.
     * @param item The IndexItem to putput
     * @param properties Some IndexProperties 
     * @return the number of byte written
     */
    public long write(IndexItem item, IndexProperties properties) throws IOException;
    

    /**
     * Flush out any remainig data.
     */
    public long flush() throws IOException;


    /**
     * Called as the first thing of doOutput().
     * Useful for doing any processing before output starts.
     */
    public Object begin() throws IOException;

    /**
     * Called as the last thing of doOutput(), just before it returns.
     * Useful for doing any processing after output has finished.
     */
    public Object end() throws IOException;

}

