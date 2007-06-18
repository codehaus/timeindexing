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
