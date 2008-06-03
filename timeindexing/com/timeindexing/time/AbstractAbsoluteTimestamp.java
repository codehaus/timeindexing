Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
