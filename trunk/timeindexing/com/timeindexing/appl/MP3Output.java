// MP3Output.java

package com.timeindexing.plugin;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexProperties;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A class to output MP3 data
 */
public class MP3Output extends SelectionPlugin implements OutputPlugin {
    /**
     * Construct an MP3Output object given
     * an index and an output stream.
     */
    public MP3Output(Index anIndex, OutputStream output) {
	super(anIndex, output);
    }

}
