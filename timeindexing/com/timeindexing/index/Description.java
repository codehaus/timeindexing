// Description.java

package com.timeindexing.index;

import java.nio.ByteBuffer;

/**
 * This class represents a description which is 
 * part of an index header.
 * The description can be any number of bytes,
 * and need not be textual.
 */
public class Description {
    // the dat itself
    ByteBuffer theData = null;
    
    // the data type.
    DataType dataType = DataType.NOTSET_DT;
    
    /**
     * Construct a description.
     */
    public Description() {
	;
    }

    /**
     * A text description.
     */
    public Description(String desc) {
	theData = ByteBuffer.wrap(desc.getBytes());
	dataType = DataType.STRING_DT;
    }
        
    /**
     * Construct a description from a byte[] and a data type.
     */
    public Description(byte[] theBytes, int type) {
	theData = ByteBuffer.wrap(theBytes);
	dataType = DataTypeDirectory.find(type);
    }

    /**
     * Construct a description from a byte[] and a data type.
     */
    public Description(byte[] theBytes, DataType type) {
	theData = ByteBuffer.wrap(theBytes);
	dataType = type;
    }

        
    /**
     * Construct a description from a byte[] and a data type.
     */
    public Description(ByteBuffer buffer, int type) {
	theData = buffer;
	dataType = DataTypeDirectory.find(type);
    }

        
    /**
     * Construct a description from a ByteBuffer and a data type.
     */
    public Description(ByteBuffer buffer, DataType type) {
	theData = buffer;
	dataType = type;
    }

    /**
     * Get the data s a ByteBuffer.
     */
    public ByteBuffer getByteBuffer() {
	return theData;
    }

    
    /**
     * Get the bytes.
     */
    public byte[] getBytes() {
	return theData.array();
    }

    /**
     * Get the data type.
     */
    public DataType getDataType() {
	return dataType;
    }

    /**
     * Get the length of the description.
     */
    public int length() {
	return theData.limit();
    }
}
