// WriterPlugin.java

package com.timeindexing.plugin;

import com.timeindexing.index.IndexItem;

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
     public long write(IndexItem item) throws IOException;


 }
