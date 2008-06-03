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
    WriterPlugin plugin = null;

    /**
     * Construct an DefaultOutputPlugin object given
     * just a WriterPlugin.
     * The Index and the OutputStream will be set later using
     * the setContext() method.
     */
    public DefaultOutputPlugin(WriterPlugin aPlugin) {
	plugin = aPlugin;
    }

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
     * an index and an output stream and a WriterPlugin.
     */
    public DefaultOutputPlugin(Index anIndex, OutputStream output, WriterPlugin aPlugin) {
	index = anIndex;
	out = output;
	setWriterPlugin(aPlugin);
    }

    /**
     * Set the context for the OutputPlugin, which is
     * the Index we are going output for, and the OutputStream
     * that is being written to.
     */
    public OutputPlugin setContext(Index anIndex, OutputStream outStream) {
	index = anIndex;
	out = outStream;
	setWriterPlugin(plugin);

	return this;
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

    /**
     * Do some output.
     * @return the number of byte written
     */
    public long write(IndexItem item, IndexProperties properties) throws IOException {
	long writeCount =  plugin.write(item, properties);

	/*
	 * This test is not useful if the OutputWriter does buffering
	if (item.getDataSize().value() != writeCount) {
	    throw new IOException("DefaultOutputPlugin: didn't write  " + item.getDataSize().value() + " bytes, wrote only " +  writeCount);
	} else {
	    return writeCount;
	}
	*/

	return writeCount;
    }
    


    /**
     * Flush out any remainig data.
     */
    public long flush() throws IOException {
	return plugin.flush();
    }

    /**
     * Does nothing.
     */
    public Object begin() throws IOException {
	return plugin.begin();
    }

    /**
     * Close the OutputStream by default.
     */
    public Object end() throws IOException {
	return plugin.end();
    }

 }
