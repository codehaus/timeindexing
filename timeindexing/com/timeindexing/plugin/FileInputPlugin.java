// FileInputPlugin.java

package com.timeindexing.plugin;

import com.timeindexing.index.Index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * A class to input a whole file.
 */
public class FileInputPlugin extends DefaultInputPlugin {
    // A lock on the file
    FileLock fileLock = null;

    /**
     * Construct an FileInputPlugin object given
     * an index and a file name.
     */
    public FileInputPlugin(Index anIndex, String fileName) throws FileNotFoundException {
	super(anIndex, new FileInputStream(fileName), new FileItem());
    }

    /**
     * Construct an FileInputPlugin object given
     * an index and a File object. 
     */
    public FileInputPlugin(Index anIndex, File file) throws FileNotFoundException {
	super(anIndex, new FileInputStream(file), new FileItem());
    }

    /**
     * Construct a FileInputPlugin object given
     * an index and an InputStream.
     */
    public FileInputPlugin(Index anIndex, FileInputStream inStream) {
	super(anIndex, inStream, new FileItem());
    }

    /**
     * Begin for this class locks the file.
     */
    public Object begin() {
	// get the InputStream as a FileInputStream
	FileInputStream fileStream = (FileInputStream)in;

	// get its Channel
	FileChannel channel = fileStream.getChannel();

	// and lock it
	try {
	    fileLock = channel.tryLock(0, Long.MAX_VALUE, true);

	    //System.err.println("Locked stream " + fileStream + ". Got " + fileLock);

	} catch (IOException ioe) {
	    System.err.println("Can;t lock stream " + fileStream);
	}

	return null;
    }

    /**
     * End for this class releases the lock.
     */
    public Object end() {
	if (fileLock != null) {
	    try {
		fileLock.release();
		//System.err.println("Lock released " + fileLock);
	    } catch (IOException ioe) {
		System.err.println("Can't release lock " + fileLock);
	    }
	}

	return null;
    }

}
