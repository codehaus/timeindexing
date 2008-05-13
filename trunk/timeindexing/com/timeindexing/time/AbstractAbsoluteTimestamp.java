// AbstractAbsoluteTimestamp.java

package com.timeindexing.time;

/**
 * An abstract base class for implementations of AbsoluteTimestamp.
 * It provides default methods for  getEpoch() and setEpoch().
 */
public abstract class AbstractAbsoluteTimestamp implements AbsoluteTimestamp {
    // The epoch being used by a timestamp.
    Epoch epoch = null;

    /**
     * Get the Epoch for the Timestamp.
     */
    public Epoch getEpoch() {
	// check if an epoch has been set
	if (epoch == null) {
	    // this is an experimental piece of code that
	    // converts AbsoluteTimestamps in the first year
	    // i.e. 1/1/1970 => 31/12/1970 into Zero Epoch
	    // and others into FirstJan1971Epoch
	    if (this.getSeconds() < (60 * 60 * 24 * 365)) {
		epoch = ZeroEpoch.EPOCH;
		return epoch;
	    } else {
		epoch = FirstJan1971Epoch.EPOCH;
		return epoch;
	    }
	} else {
	    return epoch;
	}
    }

    /**
     * Set the Epoch for the Timestamp.
     * @param epoch The Epoch to use
     */
    public Timestamp setEpoch(Epoch epoch) {
	this.epoch = epoch;
	return this;
    }


}
