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



// SelectionValidationCode.java

package com.timeindexing.appl;

import com.timeindexing.index.IndexView;
import com.timeindexing.index.GetItemException;
import com.timeindexing.index.IndexClosedException;

/**
 * This generates a validation code for an IndexView.
 * It can be used at playback time to detemine
 * if the correct selection has been requested.
 */
public class SelectionValidationCode {
    
    /**
     * Construct a SelectionValidationCode object.
     */
    public SelectionValidationCode() {
	;
    }

    /**
     * Generate a code for a particular selection,
     * specified as the IndexView returned by index.selection().
     @return 0 if there is no first or last position
     */
    public long generate(IndexView selection) {	// work out security code
	try {
	    long total = selection.getLength();
	    long base = selection.getID().value();
	    // get the first and last timestamps
	    // and lose the top bit on startV and endV
	    long startV = selection.getItem(0).getDataTimestamp().value() ^ ((long)1 << 63);
	    long endV = selection.getItem(total-1).getDataTimestamp().value() ^ ((long)1 << 63);
	    // get the first and last positions
	    long startP = selection.getItem(0).getPosition().value();
	    long endP = selection.getItem(total-1).getPosition().value();

	    // create a factor between 0 and 7 based on the start pos and end post
	    long factor = (startP + endP) % 7;

	    // shuffle the bytes of base around a bit
	    // add some values
	    // lost top bit
	    long securityCode = (((base<< (8 * factor)) | (base >>> (8 * (8-factor)))) + startV - endV) & ~((long)1<<63);

	    /*
	      System.err.print("ID = " + base);
	      System.err.print(" Start = " + startV + " " + startP);
	      System.err.print(" End = " + endV + " " + endP);
	      System.err.print(" Factor = " + factor);
	      System.err.print(" Code = " + securityCode);
	      System.err.println();
	    */

	    return securityCode;
	} catch (GetItemException gie) {
	    return 0;
	} catch (IndexClosedException ice) {
	    return 0;
	}
    }

    /**
     * Validate a code given a selection.
     */
    public boolean validate(IndexView selection, long code) {
	long expectedCode = generate(selection);

	if (expectedCode == code) {
	    return true;
	} else {
	    return false;
	}
    }
}
