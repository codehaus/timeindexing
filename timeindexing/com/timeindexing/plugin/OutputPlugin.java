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
     * Do some output.
     * @param item The IndexItem to putput
     * @param properties Some IndexProperties 
     * @return the number of byte written
     */
    public long write(IndexItem item, IndexProperties properties) throws IOException;
    

    /**
     * Called as the first thing of doOutput().
     * Useful for doing any processing before output starts.
     */
    public Object begin();

    /**
     * Called as the last thing of doOutput(), just before it returns.
     * Useful for doing any processing after output has finished.
     */
    public Object end();

    /**
     * Set a writer plugin, to read input from the InputStream.
     */
    public OutputPlugin setWriterPlugin(WriterPlugin writer);

    /**
     * Get the writer plugin.
     */
    public WriterPlugin getWriterPlugin();
}
