// TimeIndexInteractor.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.time.Timestamp;
import com.timeindexing.data.DataItem;

/**
 * This is the generic object that applications interact with.
 */
public  class TimeIndexInteractor extends TimeIndex implements Index, java.io.Serializable {
    /**
     * Construct a Time Index using the nominated proxy object.
     */
    protected TimeIndexInteractor(Index impl) {
	super(impl);
    }
}
