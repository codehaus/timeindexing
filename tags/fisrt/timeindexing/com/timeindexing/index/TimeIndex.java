// TimeIndex.java

package com.timeindexing.index;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Lifetime;
import com.timeindexing.data.DataItem;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.AbsoluteAdjustablePosition;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.AbsoluteInterval;
import com.timeindexing.basic.EndPointInterval;
import com.timeindexing.basic.Overlap;
import com.timeindexing.basic.ID;


/**
 * This is the generic object that applications interact with.
 * It is a view onto an index.
 */
public  class TimeIndex implements Index, IndexView,  Cloneable, java.io.Serializable {
    /**
     * The actual implementation of an index.
     */
    Index indexImpl = null;

    /*
     * An Interval for the selection.
     * This is a reference to the Interval that was used
     * to make a selection.
     */
    Interval selectionInterval = null;

    /*
     * The start position. By default this is the zeroth elemnt
     */
    long start = 0;
     
     /*
     * The end position. By default this is last element.
     */
    long end = 0;

    /*
     * The current navigation position in the index view.
     */
    AbsoluteAdjustablePosition position = null;

    /*
     * A mark position into the index view.
     */
    AbsoluteAdjustablePosition mark = null;

    /*
     * Is this a selection.
     */
    boolean isSelection = false;

    /*
     * Is this terminated.
     * This variable is only used when a selection is taken.
     */
    public boolean isTerminated = false;

   /**
     * Construct a Time Index using the nominated Index object.
     */
    protected TimeIndex(Index impl) {
	indexImpl = impl;
	start = 0;
	end = indexImpl.getLength()-1;
	position = new AbsoluteAdjustablePosition(0);
    }

        /**
     * The name of the index.
     */
    public String getName() {
	return indexImpl.getName();
    }

    /**
     * Get an ID of an index.
     */
    public ID getID() {
	return indexImpl.getID();
    }

     /**
      * Get the start time of the index.
      * This is when the index was created not necessarliy when the first item
      * was added to the index.
      */
    public Timestamp getStartTime() {
	return indexImpl.getStartTime();
    }

     /**
      * Get the end time of the index.
      * This is the time the last item was closed, not necessarliy when the last item
      * was added to the index.
      */
    public Timestamp getEndTime() {
	return indexImpl.getEndTime();
    }

    /**
     * Get the time the first IndexItem was put into the Index.
     */
    public Timestamp getFirstTime() {
	if (isSelection) {
	    IndexItem first = getItem(0);
	    return first.getIndexTimestamp();
	} else {
	    return indexImpl.getFirstTime();
	}
    }

    /**
     * Get the time the last IndexItem was put into the Index.
     */
    public Timestamp getLastTime() {
	if (isSelection) {
	    IndexItem last = getItem(getLength()-1);
	    return last.getIndexTimestamp();
	} else {
	    return indexImpl.getLastTime();
	}
    }


    /**
     * Get the data time for the first IndexItem in the Index.
     */
    public Timestamp getFirstDataTime() {
	if (isSelection) {
	    IndexItem first = getItem(0);
	    return first.getDataTimestamp();
	} else {
	    return indexImpl.getFirstDataTime();
	}
    }

    /**
     * Get the data time for the last IndexItem in the Index.
     */
    public Timestamp getLastDataTime() {
	if (isSelection) {
	    IndexItem last = getItem(getLength()-1);
	    return last.getDataTimestamp();
	} else {
	    return indexImpl.getLastDataTime();
	}
    }

    /**
     * Get the size of the index items.
     */
    public int getItemSize() {
	return indexImpl.getItemSize();
    }


    /**
     * Does the index have fixed size data.
     */
    public boolean isFixedSizeData() {
	return indexImpl.isFixedSizeData();
    }


    /**
     * Get the size of the data items, if there is fixed size data.
     */
    public long getDataSize() {
	return indexImpl.getDataSize();
    }


    /**
     * Get the type name of the things in the data stream.
     */
    public String getDataType(ID typeID) {
	return indexImpl.getDataType(typeID);
    }

    /**
     * Does this index have a typed name.
     */
    public boolean hasDataType(String typeName) {
	return indexImpl.hasDataType(typeName);
    }


    /**
     * Add a new data type
     * @return true, if a new type was added; false, if the index had this ID/typeName pair already
     */
    public boolean addDataType(ID typeID, String typeName) {
	return indexImpl.addDataType(typeID, typeName);
    }

    /**
     * Get the data style.
     * Either inline or external.
     */
    public int getDataStyle() {
	return indexImpl.getDataStyle();
    }


    /**
     * Does this index have annotations.
     */
    public boolean hasAnnotations() {
	return indexImpl.hasAnnotations();
    }

    /**
     * Get the annotation style.
     * Either inline or external.
     */
    public int getAnnotationStyle() {
	return indexImpl.getAnnotationStyle();
    }


    /**
     * Get the index URI of a nominated index.
     */
    public String getIndexURI(ID indexID) {
	return indexImpl.getIndexURI(indexID);
    }

    /**
     * Does this index have the URI of some other index
     */
    public boolean hasIndexURI(String URIName) {
	return indexImpl.hasIndexURI(URIName);
    }

    /**
     * Add a new indexID/indexURI
     * @return true, if a new index URI was added; false, if the index had this ID/URI pair already
     */
    public boolean addIndexURI(ID indexID, String URIName) {
	return indexImpl.addIndexURI(indexID, URIName);
    }

    /**
     * Add a Data Item to the Index.
     */
    public long addItem(DataItem item) {
	if (isSelection) {
	    throw new IndexTerminatedException("Can't add data to an Index selection");
	} else {
	    return indexImpl.addItem(item);
	}
    }

    /**
     * Add a Data Item to the Index with a specific Data Timestamp
     */
    public long addItem(DataItem item, Timestamp datatime) {
	if (isSelection) {
	    throw new IndexTerminatedException("Can't add data to an Index selection");
	} else {
	    return indexImpl.addItem(item, datatime);
	}
    }

    /**
     * Get the no of items in the index.
     */
    public long getLength() {
	if (isSelection) {
	    return end - start + 1;
	} else {
	    return indexImpl.getLength();
	}
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n) {
	if (isSelection) {
	    return indexImpl.getItem(n+start);
	} else {
	    return indexImpl.getItem(n);
	}
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p) {
	if (isSelection) {
	    return indexImpl.getItem((Position)new AbsoluteAdjustablePosition(p).adjust(start));
	} else {
	    return indexImpl.getItem(p);
	}
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime) {
	TimestampMapping tsm = locate(t, sel, lifetime);
	return getItem(tsm.position());
    }

    /**
     * Hollow the IndexItem at the position.
     */
    public boolean hollowItem(long position) {
	if (isSelection) {
	    return indexImpl.hollowItem(position+ start);
	} else {
	    return indexImpl.hollowItem(position);
	}
    }

    /**
     * Hollow the IndexItem at the position.
     */
    public boolean hollowItem(Position p) {
	if (isSelection) {
	    return indexImpl.hollowItem((Position)new AbsoluteAdjustablePosition(p).adjust(start));
	} else {
	    return indexImpl.hollowItem(p);
	}
    }
	
    /**
     * Does a timestamp fall within the bounds of the Index.
     * The bounds are the first time data is put in and the last
     * time data is put in the Index.
     */
    public boolean contains(Timestamp t, IndexTimestampSelector sel) {
	if (isSelection) {
	    IndexItem first = getItem(0);
	    IndexItem last = getItem(getLength()-1);
	    Timestamp firstTS =  null;
	    Timestamp lastTS = null;

	    if (sel == IndexTimestampSelector.DATA) {
		firstTS = first.getDataTimestamp();
		lastTS = last.getDataTimestamp();
	    } else {
		firstTS =  first.getIndexTimestamp();
		lastTS = last.getIndexTimestamp();
	    }

	    if (TimeCalculator.lessThanEquals(firstTS, t) &&
		TimeCalculator.lessThanEquals(t, lastTS)) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return indexImpl.contains(t, sel);
	}
    }

    /**
     * Try and determine the position associated
     * with the speicifed data Timestamp.
     */
    public TimestampMapping locate(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime) {
	if (isSelection) {
	    if (contains(t, sel)) {
		return indexImpl.locate(t, sel, lifetime);
	    } else {
		// now try and determine if it is too low or too high
		IndexItem first = getItem(0);
		IndexItem last = getItem(getLength()-1);
		Timestamp firstTS =  null;
		Timestamp lastTS = null;

		if (sel == IndexTimestampSelector.DATA) {
		    firstTS = first.getDataTimestamp();
		    lastTS = last.getDataTimestamp();
		} else {
		    firstTS =  first.getIndexTimestamp();
		    lastTS = last.getIndexTimestamp();
		}

		if (TimeCalculator.lessThan(t, firstTS)) {
		    return new TimestampMapping(t, Position.TOO_LOW);
		} else if (TimeCalculator.greaterThan(t, lastTS)) {
		    return new TimestampMapping(t, Position.TOO_HIGH);
		} else {
		    throw new Error("TimeIndex: locate failed to determine timestamp");
		}		
	    }
	} else {
	    return indexImpl.locate(t, sel, lifetime);
	}
    }


    /**
     * Select an Interval.
     * Returns null if it cant be done.
     */
    public IndexView select(Interval interval, IndexTimestampSelector selector, Overlap overlap, Lifetime lifetime) {
	if (interval instanceof AbsoluteInterval) {
	    AbsoluteInterval absInterval = (AbsoluteInterval)interval;
	    AbsoluteInterval resolvedInterval = null;

	    if (! absInterval.isResolved()) { // not resolved yet
		// so resolve the interval w.r.t this index
		resolvedInterval = absInterval.resolve(this, selector, lifetime);
	    } else {
		resolvedInterval = absInterval;
	    }

	    if (resolvedInterval == null) { // it was not possible to resolved the Interval
		return null;
	    } else {
		long startV = resolvedInterval.start().value();
		long endV = resolvedInterval.end().value();

		if (overlap == Overlap.STRICT) {
		    // if the Interval must have strict over lap with 
		    // the index then complain if the start or the end
		    // position is out of bounds

		    if (startV < 0) {
			throw new PositionOutOfBoundsException("Can't select before the start of an Index");
		    }

		    if (endV > end) {
			throw new PositionOutOfBoundsException("Can't select beyond the end of an Index");
		    }
		} else {
		    // if the Interval can overlap freely with 
		    // the index then do some tweaking if the start or the end
		    // position is out of bounds

		    if (startV < 0) {
			startV = 0;
		    }

		    if (endV > end) {
			endV = end;
		    }
		}

		try {
		    TimeIndex selection = (TimeIndex)this.clone();
		    selection.selectionInterval = interval;
		    selection.start = start +  startV;
		    selection.end = start + endV;
		    selection.isSelection = true;
		    selection.isTerminated = true;
		    selection.position = new AbsoluteAdjustablePosition(0);
		    selection.mark = null;

		    return selection;
		} catch ( CloneNotSupportedException cnse) {
		    // the clone failed
		    throw new Error("TimeIndex: clone() failed");
		}
	    }
	} else {
	    return null;
	}
    }

    /**
     * Is this a selection.
     */
    public boolean isSelection() {
	return isSelection;
    }

    /**
     * Is the Index activated.
     */
    public boolean isActivated() {
	return indexImpl.isActivated();
    }

    /**
     * Make the Index activated.
     * It is not possible to add items to an Index
     * that is not active.
     */
    public Index activate() {
	return indexImpl.activate();
    }

    /**
     * Is the Index terminated.
     */
    public boolean isTerminated() {
	return indexImpl.isTerminated();
    }

    /**
     * Make the Index finalized.
     * It is not possible to add items ever again
     * to an Index that has been terminated.
     */
    public Index terminate() {
	return indexImpl.terminate();
    }

    /**
     * Get the  last time an IndexItem was accessed from the index.
     */
    public Timestamp getLastAccessTime() {
	return indexImpl.getLastAccessTime();
    }


    /**
     * Flush this index.
     */
    public boolean flush() {
	return indexImpl.flush();
    }

    /**
     * Close the index.
     * Intened to close all associated streams and files,
     * and this sets the end time too.
     */
    public boolean close() {
	return indexImpl.close();
    }

    /**
     * Clone me
     */
    public Object clone() throws  CloneNotSupportedException {
	return super.clone();
    }

    /*
     * Navigation methods.
     */

    /**
     * Gets the current navigation position into the IndexView.
     */
    public Position position() {
	return position;
    }

    /**
     * Sets the current navigation position into the IndexView.
     */
    public IndexView position(Position p) {
	if (0 <= p.value() && p.value() <= (getLength()-1)) {
	    position = new AbsoluteAdjustablePosition(p);
	    return this;
	} else {
	    throw new PositionOutOfBoundsException("TimeIndex: position(): Can;t set the position outside if the IndexView");
	}
    }

    /**
     * Sets the current navigation position into the IndexView.
     */
    public IndexView position(long n) {
	return position(new AbsoluteAdjustablePosition(n));
    }

    /**
     * Sets the mark into the IndexView, using the current navigation position
     * as the mark value.
     */
    public IndexView mark() {
	mark = position;
	return this;
    }

    /**
     * Sets the current navigation position into the IndexView
     * to be one forward.
     */
    public IndexView forward() {
	if (position.value() == getLength()-1) {
	    // at the end
	    // do nothing
	    return this;
	} else {
	    position.adjust(+1);
	    return this;
	}
    }

    /**
     * Sets the current navigation position into the IndexView
     * to be one backward.
     */
    public IndexView backward() {
	if (position.value() == 0) {
	    // at the start
	    // do nothing
	    return this;
	} else {
	    position.adjust(-1);
	    return this;
	}
    }


    /**
     * Get the Index Item from the Index at position position().
     */
    public IndexItem getItem() {
	if (position == null) {
	    throw new PositionOutOfBoundsException("Position not set. Region does not exist");
	} else {
	    return getItem(position);
	}
    }

    /**
     * Get the Index Item from the Index at position mark().
     */
    public IndexItem getItemAtMark() {
	if (mark == null) {
	    throw new PositionOutOfBoundsException("Mark not set. Region does not exist");
	} else {
	    return getItem(mark);
	}
    }

    /**
     * What is the region covered by position and mark.
     * Returned value is an Interval.
     */
    public Interval region() {
	if (position == null) {
	    throw new PositionOutOfBoundsException("Position not set. Region does not exist");
	} else if (mark == null) {
	    throw new PositionOutOfBoundsException("Mark not set. Region does not exist");
	} else {
	    return new EndPointInterval(mark, position);
	}
    }

    /**
     * Exchanges the mark into the IndexView, with the current navigation position.
     */
    public IndexView exchange() {
	AbsoluteAdjustablePosition temp = position;
	position = mark;
	mark = temp;
	return this;
    }

}


