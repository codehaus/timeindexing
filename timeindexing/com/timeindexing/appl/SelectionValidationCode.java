// SelectionValidationCode.java

package com.timeindexing.appl;

import com.timeindexing.index.IndexView;

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
     */
    public long generate(IndexView selection) {	// work out security code
	long total = selection.getLength();
	long base = selection.getID().value();
	// lose top bit on startV and endV
	long startV = selection.getItem(0).getDataTimestamp().value() ^ ((long)1 << 63);
	long endV = selection.getItem(total-1).getDataTimestamp().value() ^ ((long)1 << 63);
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
