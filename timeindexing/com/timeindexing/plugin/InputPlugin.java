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
// InputPlugin.java

package com.timeindexing.plugin;

import com.timeindexing.index.Index;
import java.io.InputStream;
import java.io.IOException;

/**
 * An interface for input plugins.
 */
public interface InputPlugin {
    /**
     * Get the index we are doing input for.
     */
    public Index getIndex();

    /**
     * Get the InputStream for the InputPlugin.
     */
    public InputStream getInputStream();

    /**
     * Do some input.
     */
    public ReaderResult read() throws IOException;

    /**
      * Determine if the reader has hit EOF.
      */
     public boolean isEOF();

    /**
     * Called as the first thing of doInput().
     * Useful for doing any processing before input starts.
     */
    public Object begin();

    /**
     * Called as the last thing of doInput(), just before it returns.
     * Useful for doing any processing after input has finished.
     */
    public Object end();

    /**
     * Set a reader plugin, to read input from the InputStream.
     */
    public InputPlugin setReaderPlugin(ReaderPlugin reader);

    /**
     * Get the reader plugin.
     */
    public ReaderPlugin getReaderPlugin();
}
