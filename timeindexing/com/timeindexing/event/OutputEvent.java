// IndexAccessEvent.java

package com.timeindexing.event;

import com.timeindexing.basic.ID;
import java.util.EventObject;

/**
 * An OutputEvent, which is generated 
 * when an  output component actually does some output.
 */
public class OutputEvent extends IndexEvent {
    /*
     * The number of bytes output.
     */
    long bytesOutput = 0;

    /**
     * Construct an IndexAccessEvent.
     * Constructed from an index name and the number of bytes
     * that have been output.
     */
    public OutputEvent(String aName, ID anID, long bytesOutput, Object aSource) {
	super(aName, anID, aSource);
	this.bytesOutput = bytesOutput;
    }

    /**
     * Get the number of bytes output.
     */
    public long getBytesOutput() {
	return bytesOutput;
    }
}
