/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



// AID.java

package com.timeindexing.basic;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * This is an abstract ID.
 * The constructor generates an ID which cannot be changed.
 */

public abstract class AID implements ID {
    /** A simple ID, using a long.*/
    long idValue;

    /**
     * Get the ID as a long value.
     */
    public long value() {
	return idValue;
    }

    /**
     * Determine if two uid's are equal.
     */
    public boolean equals(Object other) {
	if (ID.class.isInstance(other)) {
	    if (value() == ((ID)other).value()) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    /**
     * Return a hashCode.
     * ID's with the same idValue should have the same hashcode.
     * This is defined mostly for the benefit of the ReconstructedID
     * subclass.
     */
    public int hashCode() {
	int hash = (int)idValue;
	return hash;
    }

    /**
     * A default string representation of an ID.
     * It merely prints the value of the ID.
     */
    public String toString() {
       return ""+value();
    }

    /** 
     * Write out the ID.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
	out.writeLong(idValue);
    }

    /** 
	8 Read in the ID.
     */ 
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	idValue = in.readLong();
    }
}


