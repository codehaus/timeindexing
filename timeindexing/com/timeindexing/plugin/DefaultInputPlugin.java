/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



// DefaultInputPlugin.java

package com.timeindexing.plugin;

import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;

import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A class to input any  data
 */
public class DefaultInputPlugin implements InputPlugin {
    Index index = null;
    InputStream in = null;
    ReaderPlugin plugin = null;

    private final static int BUFSIZE = 1024;

    /**
     * Construct an DefaultInputPlugin object given
     * an index and an input stream.
     * Uses the Line reader plugin, which reads a line at a time.
     */
    public DefaultInputPlugin(Index anIndex, InputStream input) {
	index = anIndex;
	in = input;
	setReaderPlugin(new Line(in));
    }

    /**
     * Construct an DefaultInputPlugin object given
     * an index and an input stream.
     */
    public DefaultInputPlugin(Index anIndex, InputStream input, ReaderPlugin aPlugin) {
	index = anIndex;
	in = input;
	setReaderPlugin(aPlugin);
    }

    /**
     * Get the index we are doing input for.
     */
    public Index getIndex() {
	return index;
    }

    /**
     * Get the InputStream for the InputPlugin.
     */
    public InputStream getInputStream() {
	return in;
    }

    /**
     * Do some input.
     * @return the number of byte written
     */
    public ReaderResult read() throws IOException {
	return plugin.read();
    }
    


    /**
      * Determine if the reader has hit EOF.
      */
    public boolean isEOF() {
	return plugin.isEOF();
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
     * Set a reader plugin, to read input from the InputStream.
     */
    public InputPlugin setReaderPlugin(ReaderPlugin reader) {
	plugin = reader;
	plugin.setInputStream(this.getInputStream());
	return this;
    }

    /**
     * Get the reader plugin.
     */
    public ReaderPlugin getReaderPlugin() {
	return plugin;
    }


 }
