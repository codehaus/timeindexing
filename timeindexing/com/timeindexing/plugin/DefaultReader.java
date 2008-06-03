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
// DefaultReader.java

package com.timeindexing.plugin;

import com.timeindexing.index.IndexItem;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.IOException;

/**
 * An default reader plugin.
 * Takes an IndexItem and writes the bytes to the input stream.
 */
public abstract  class DefaultReader implements ReaderPlugin {
    InputStream in = null;
    boolean eof = false;

    /**
     * Determine if the reader has hit EOF.
     */
    public  boolean isEOF() {
	return eof;
    }


    /**
     *  The reader has hit EOF.
     */
    public  ReaderPlugin setEOF() {
	eof = true;
	return this;
    }

     /**
     * Get the InputStream for the InputPlugin.
     */
    public InputStream getInputStream() {
	return in;
    }

    /**
     * Set the InputStream for the InputPlugin.
     */
    public ReaderPlugin setInputStream(InputStream inStream) {
	in = inStream;
	return this;
    }
  
 }
