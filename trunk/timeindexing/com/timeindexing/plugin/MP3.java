// MP3.java

package com.timeindexing.plugin;


import com.timeindexing.time.Timestamp;
import com.timeindexing.time.NanosecondTimestamp;
import com.timeindexing.time.NanosecondElapsedFormat;
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
public class MP3 implements ReaderPlugin {
    InputStream input = null;
    boolean eof = false;
    Bitstream bitstream = null;
    Header header = null;
    NanosecondTimestamp elapsedTime = null;

    /**
     * Construct a Mp3 plugin from an InputStream.
     */
    public MP3(InputStream inStream) {
	input = inStream;
	bitstream = new Bitstream(inStream);
	elapsedTime = new NanosecondTimestamp(0);
    }

    /**
     * Get next input buffer.
     */
    public ReaderResult read() throws IOException {
	try {
	    if ((header = bitstream.readFrame()) == null) {
		// hit EOF
		eof = true;
		System.err.print('\n');
		return null;
	    } else {
		// return the sample
		return process();
	    }
	} catch (BitstreamException be) {
	    throw new IOException(be.getMessage());
	}
    }
    /**
     * Determine if the reader has hit EOF.
     */
    public  boolean isEOF() {
	return eof;
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

	//System.err.print("framesize = " + framesize);
	//System.err.print(" buf size = " + buffer.limit());
	//System.err.print(" ms/frame = " + millis);
	//System.err.print(" nanos = " + frameTime);
	System.err.print( NanosecondElapsedFormat.fullFormat(elapsedTime));
	System.err.print('\r');

	bitstream.closeFrame();

	// add this frame's elapsed time onto the total time
	elapsedTime = (NanosecondTimestamp)TimeCalculator.addTimestamp(elapsedTime, frameTime);

	// return the mp3 sample as a ReaderResult
	return result;
    }


}
