// FileItem.java

package com.timeindexing.plugin;

import com.timeindexing.time.Timestamp;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;


/**
 * A plugin that takes a file input stream and
 * returns the whole file.
 */
public class FileItem implements ReaderPlugin {
    FileInputStream input = null;
    MappedByteBuffer buffer = null;
    String line = null;
    boolean eof = false;

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
	    System.err.println("FileItem: size = " + buffer.capacity() + 
			       " is loaded = " + buffer.isLoaded());

	    // hit EOF
	    eof = true;

	    // return the buffer
	    return new DefaultReaderResult(buffer, null);
	}
    }

    /**
     * Determine if the reader has hit EOF.
     */
    public  boolean isEOF() {
	return eof;
    }
}
