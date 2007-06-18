// Line.java

package com.timeindexing.plugin;

import com.timeindexing.time.Timestamp;
import com.timeindexing.index.DataType;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;


/**
 * A plugin that takes an input stream and
 * returns a line at a time.
 */
public class Line extends DefaultReader implements ReaderPlugin {
    BufferedReader input = null;
    String line = null;

    /**
     * Construct a Line plugin from an InputStream.
     */
    public Line(InputStream inStream) {
	input = new BufferedReader (new InputStreamReader(inStream));
    }

    /**
     * Construct a Line plugin from a Reader.
     */
    public Line(Reader inReader) {
	input = new BufferedReader (inReader);
    }

    /**
     * Construct a Line plugin from a BufferedReader.
     */
    public Line(BufferedReader inReader) {
	input = inReader;
    }

    /**
     * Get next input buffer.
     */
    public ReaderResult read() throws IOException {
	if ((line = input.readLine()) == null) {
	    // hit EOF
	    eof = true;
	    eofProcess();
	    return null;
	} else {
	    // return the processed line and add back the EOL 
	    return process(line);
	}
    }

    /**
     * Process the line
     */
    protected ReaderResult process(String line) {
	// return the line as a ReaderResult
	// allocate a Buffer big enough for the line plus a newline
	byte [] lineBytes = line.getBytes();
	ByteBuffer lineBuffer = ByteBuffer.allocate(lineBytes.length + 1);
	lineBuffer.put(lineBytes);
	lineBuffer.put(System.getProperty("line.separator").getBytes());
	return new DefaultReaderResult(lineBuffer, null, DataType.TEXT);
    }

    /**
     * Processing at EOF.
     * Return values states if something happended.
     */
    protected boolean eofProcess() {
	return false;
    }
}
