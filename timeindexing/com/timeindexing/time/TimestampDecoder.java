// TimestampDecoder.java

package com.timeindexing.time;

import java.util.logging.Logger;

/**
 * A TimestampDecoder takes a long and tries to determine the Timestamp
 * object that is associated with that format.
 */
public class TimestampDecoder {
    /**
     * Decode a long and return the relevant Timestamp
     */
    public Timestamp decode(long value) {
	// special case if value is 0
	if (value == 0) {
	    return Timestamp.ZERO;
	}

	//check top 2 bits
	long valueT = value & Timestamp.TOP_2_MASK;

	//Logger.getLogger("global").info("Top 2 bits = " + (valueT >>> 62) + " value = " + (value & ~Timestamp.TOP_2_MASK));

	if (valueT == Timestamp.MILLISECOND) {
	    return new MillisecondTimestamp(value);

	} else if (valueT == Timestamp.MICROSECOND) {
	    return new MicrosecondTimestamp(value);

	} else if (valueT == Timestamp.NANOSECOND) {
	    return new NanosecondTimestamp(value);

	} else if (valueT == Timestamp.EXTENDED) {

	    valueT = value & Timestamp.TOP_4_MASK;
	    
	    //Logger.getLogger("global").info("Top 4 bits = " + (valueT >>> 60) + " value = " + (value & ~Timestamp.TOP_4_MASK));

	    if (valueT == Timestamp.ELAPSED_MILLISECOND) {
		return new ElapsedMillisecondTimestamp(value);

	    } else if (valueT == Timestamp.ELAPSED_MICROSECOND) {
		return new ElapsedMicrosecondTimestamp(value);

	    } else if (valueT == Timestamp.SECOND) {
		return new SecondTimestamp(value);

	    } else if (valueT == Timestamp.EXTENDED_2) {

		valueT = value & Timestamp.TOP_6_MASK;

		//Logger.getLogger("global").info("Top 6 bits = " + (valueT >>> 58)+ " value = " + (value & ~Timestamp.TOP_6_MASK));

		if (valueT == Timestamp.ELAPSED_NANOSECOND) {
		    return new ElapsedNanosecondTimestamp(value);

		} else if (valueT == Timestamp.ELAPSED_SECOND) {
		    return new ElapsedSecondTimestamp(value);

		} else if (valueT == Timestamp.SPARE_3E) {
		    // TODO: define the SPARE_3E Timestamp object
		    throw new TimestampDecoderException("Timestamp SPARE_3E not defined yet!!");

		} else if (valueT == Timestamp.EXTENDED_3) {

		    valueT = value & Timestamp.TOP_8_MASK;

		    if (valueT == Timestamp.ELAPSED_UNITS) {
			return new MPEGPresentationTimestamp(value);

		    } else if (valueT == Timestamp.SPARE_FD) {
			// TODO: define the SPARE_FD Timestamp object
			throw new TimestampDecoderException("Timestamp SPARE_FD not defined yet!!");

		    } else if (valueT == Timestamp.SPARE_FE) {
			// TODO: define the SPARE_FE Timestamp object
			throw new TimestampDecoderException("Timestamp SPARE_FE not defined yet!!");

		    } else {
			throw new TimestampDecoderException("TimestampDecoderException: No known time with top 8 bits: " + ((valueT & Timestamp.TOP_8_MASK) >>> 58));
		    }

		} else {
		    throw new TimestampDecoderException("TimestampDecoderException: No known time with top 6 bits: " + ((valueT & Timestamp.TOP_6_MASK) >>> 58));
		}

	    } else {
		throw new TimestampDecoderException("TimestampDecoderException: No known time with top 4 bits: " + ((valueT & Timestamp.TOP_4_MASK) >>> 60));
	    }

	} else {
	    throw new TimestampDecoderException("TimestampDecoderException: No known time with top 2 bits: " + ((valueT & Timestamp.TOP_2_MASK) >>> 62));
	}
    }

	    
}
