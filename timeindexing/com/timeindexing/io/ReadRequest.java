// ReadRequest.java


package com.timeindexing.io;

import com.timeindexing.basic.Position;

/**
 * A class that represents a read request in the I/O thread.
 * It holds data on the Position being requested and a Boolean
 * stating if the data should be loaded with the IndexItem.
 */
class ReadRequest {
    Position position;
    boolean doLoadData;

    /**
     * Construct a ReadRequest.
     */
    public ReadRequest(Position p, boolean load) {
	position = p;
	doLoadData = load;
    }
}

