// MP3.java

package com.timeindexing.plugin;


import com.timeindexing.time.Timestamp;
import com.timeindexing.time.NanosecondTimestamp;
import com.timeindexing.time.TimeCalculator;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.BitstreamException;

/**
 * A plugin that takes an MP3 input stream and
 * returns a sample at a time.
 */
public class MP3 extends DefaultReader implements ReaderPlugin {
    Bitstream bitstream = null;
    Header header = null;
    NanosecondTimestamp elapsedTime = null;

    /**
     * Construct a Mp3 plugin from an InputStream.
     */
    public MP3(InputStream inStream) {
	setInputStream(inStream);
	elapsedTime = new NanosecondTimestamp(0);
    }

    /**
     * Set the InputStream for the InputPlugin.
     */
    public ReaderPlugin setInputStream(InputStream inStream) {
	in = inStream;
	bitstream = new Bitstream(inStream);
	return this;
    }

   /**
     * Get next input buffer.
     */
    public ReaderResult read() throws IOException {
	try {
	    if ((header = bitstream.readFrame()) == null) {
		// hit EOF
		eof = true;
		return null;
	    } else {
		// return the sample
		return process();
	    }
	} catch (BitstreamException be) {
	    be.printStackTrace(System.err);
	    throw new IOException(be.getMessage());
	}
    }

    /**
     * Process the header
     */
    protected ReaderResult process() {
	int framesize = header.framesize;
	float millis = header.ms_per_frame();
	NanosecondTimestamp frameTime = new NanosecondTimestamp((long)(millis*1000000));

	// get the raw frame data
	byte [] frameData = bitstream.getFrame();

	// allocate a buffer for the data
	ByteBuffer buffer = ByteBuffer.allocate(framesize+4);

	// put the header in the buffer
	buffer.putInt(header.getSyncHeader());

	// copy the data into the buffer
	buffer.put(frameData, 0, framesize);
	buffer.limit(framesize+4);
	buffer.position(0);


	// create a Result object
	DefaultReaderResult result = new DefaultReaderResult(buffer, elapsedTime);

	bitstream.closeFrame();

	// add this frame's elapsed time onto the total time
	elapsedTime = (NanosecondTimestamp)TimeCalculator.addTimestamp(elapsedTime, frameTime);

	// return the mp3 sample as a ReaderResult
	return result;
    }


}
