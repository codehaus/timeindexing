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
