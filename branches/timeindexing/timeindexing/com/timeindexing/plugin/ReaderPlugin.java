// ReaderPlugin.java

package com.timeindexing.plugin;

import java.io.IOException;

/**
 * An interface for reader plugins.
 */
public interface ReaderPlugin {
     /**
      * Read some data and return it as a ReaderResult.
      */
     public ReaderResult read() throws IOException;

     /**
      * Determine if the reader has hit EOF.
      */
     public boolean isEOF();
 }
