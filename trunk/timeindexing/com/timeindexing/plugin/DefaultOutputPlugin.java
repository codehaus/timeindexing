// DefaultOutputPlugin.java

package com.timeindexing.plugin;

import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;

import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A class to output any  data
 */
public class DefaultOutputPlugin implements OutputPlugin {
    Index index = null;
    OutputStream out = null;
    long writeCount = 0;
    WriterPlugin plugin = null;

    private final static int BUFSIZE = 1024;

    /**
     * Construct an DefaultOutputPlugin object given
     * an index and an output stream.
     * Uses the DefaultWriter plugin.
     */
    public DefaultOutputPlugin(Index anIndex, OutputStream output) {
	index = anIndex;
	out = output;
	setWriterPlugin(new DefaultWriter());
    }

    /**
     * Construct an DefaultOutputPlugin object given
     * an index and an output stream.
     */
    public DefaultOutputPlugin(Index anIndex, OutputStream output, WriterPlugin aPlugin) {
	index = anIndex;
	out = output;
	setWriterPlugin(aPlugin);
    }

    /**
     * Get the index we are doing output for.
     */
    public Index getIndex() {
	return index;
    }

    /**
     * Get the OutputStream for the OutputPlugin.
     */
    public OutputStream getOutputStream() {
	return out;
    }

    /**
     * Do some output.
     * @return the number of byte written
     */
    public long write(IndexItem item, IndexProperties properties) throws IOException {
	return plugin.write(item, properties);
    }
    

    /**
     * Does nothing.
     */
    public Object begin() {
	return null;
    }

    /**
     * Does nothing.
     */
    public Object end() {
	return null;
    }
   
    /**
     * Set a writer plugin, to read input from the InputStream.
     */
    public OutputPlugin setWriterPlugin(WriterPlugin writer) {
	plugin = writer;
	plugin.setOutputStream(this.getOutputStream());
	return this;
    }

    /**
     * Get the writer plugin.
     */
    public WriterPlugin getWriterPlugin() {
	return plugin;
    }


 }
