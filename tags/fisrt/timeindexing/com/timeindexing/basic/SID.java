// SID.java

package com.timeindexing.basic;

import java.io.Serializable;

/**
 * This represents a specified ID.
 * The value for the ID is passed to the constructor.
 */
public class SID extends AID implements ID, Serializable {

    /**
     * Create a new ID object from 
     * This will be initialised with  some arbitrary value,
     * which may not be unique
     */
    public SID(long arbitrary) {
	idValue = arbitrary;
    }
}

