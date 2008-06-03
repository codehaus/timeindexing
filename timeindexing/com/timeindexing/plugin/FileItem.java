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
// FileItem.java

package com.timeindexing.plugin;

import com.timeindexing.time.Timestamp;
import com.timeindexing.index.DataType;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;


/**
 * A plugin that takes a file input stream and
 * returns the whole file.
 */
public class FileItem implements ReaderPlugin {
    FileInputStream input = null;
    MappedByteBuffer buffer = null;
    boolean eof = false;

    /**
     * Construct a FileItem plugin.
     * The input will be set by the InputPlugin.
     */
    public FileItem() {
    }

    /**
     * Construct a FileItem plugin from a file name
     */
    public FileItem(String filename) throws FileNotFoundException {
	input = new FileInputStream(filename);
    }

    /**
     * Construct a FileItem plugin from an InputStream.
     */
    public FileItem(FileInputStream inStream) {
	input = inStream;
    }

    /**
     * Get next input buffer, which will be the whole file
     */
    public ReaderResult read() throws IOException {
	if (isEOF()) {
	    return null;
	} else {
	    // convert the input stream to a channel
	    FileChannel inputChannel = input.getChannel();


	    // now memory map the file in
	    buffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, 0, inputChannel.size());
	    //System.err.println("FileItem: size = " + buffer.capacity() + 
	    //	                 " is loaded = " + buffer.isLoaded());

	    // hit EOF
	    setEOF();

	    // return the buffer
	    return new DefaultReaderResult(buffer, null, DataType.ANY);
	}
    }

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
	return input;
    }

    /**
     * Set the InputStream for the InputPlugin.
     * This must be a FileInputStream.
     * @return null if the InputStream is NOT a FileInputStream.
     */
    public ReaderPlugin setInputStream(InputStream inStream) {
	if (inStream instanceof FileInputStream) {
	    input = (FileInputStream)inStream;
	    return this;
	} else {
	    return null;
	}
    }
  }
