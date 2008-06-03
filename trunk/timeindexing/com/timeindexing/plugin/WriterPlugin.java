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
// WriterPlugin.java

package com.timeindexing.plugin;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexProperties;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An interface for writer plugins.
 */
public interface WriterPlugin {
    /**
     * Get the OutputStream for the OutputPlugin.
     */
    public OutputStream getOutputStream();

    /**
     * Set the OutputStream for the OutputPlugin.
     */
    public WriterPlugin setOutputStream(OutputStream out);
  
     /**
      * 
      */
     public long write(IndexItem item, IndexProperties properties) throws IOException;

    /**
     * Flush out any remainig data.
     */
    public long flush() throws IOException;

    /**
     * Called as the first thing.
     * Useful for doing any processing before output starts.
     */
    public Object begin() throws IOException;

    /**
     * Called as the last thing.
     * Useful for doing any processing after output has finished.
     */
    public Object end() throws IOException;


 }
