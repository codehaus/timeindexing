// ReaderPlugin.java

package com.timeindexing.plugin;

import java.io.IOException;
import java.io.InputStream;

/**
 * An interface for reader plugins.
 */
public interface ReaderPlugin {
    /**
     * Get the InputStream for the InputPlugin.
     */
    public InputStream getInputStream();

    /**
     * Set the InputStream for the InputPlugin.
     */
    public ReaderPlugin setInputStream(InputStream in);
  
      /**
      * Read some data and return it as a ReaderResult.
      */
     public ReaderResult read() throws IOException;

     /**
      * Determine if the reader has hit EOF.
      */
     public boolean isEOF();
 }
