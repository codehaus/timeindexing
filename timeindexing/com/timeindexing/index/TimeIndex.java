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
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.AdjustablePosition;
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
    Position start = null;
     
     /*
     * The end position. By default this is last element.
     */
    Position end = null;

    /**
     * The length of a selection
     */
    long selectionLength = 0;

    /*
     * The current navigation position in the index view.
     */
    Position position = null;

    /*
     * A mark position into the index view.
     */
    Position mark = null;

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
	start = new AbsolutePosition(0);

	//end = new AbsolutePosition(indexImpl.getLength()-1);
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
     * Get the type of an index.
     */
    public int getIndexType() {
	return indexImpl.getIndexType();
    }

   /**
     * Get the index data type.
     * Some indexes have the same type throughout,
     * other have mixed type data.
     */
    public DataType getIndexDataType() {
	return indexImpl.getIndexDataType();
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
     * @return ZeroTimestamp if there is no first item, usually when there are no items in the index
     */
    public Timestamp getFirstTime() {
	if (isSelection) {
	    try {
		IndexItem first = getItem(0);
		return first.getIndexTimestamp();
	    } catch (GetItemException gie) {
		return Timestamp.ZERO;
	    }
	} else {
	    return indexImpl.getFirstTime();
	}
    }

    /**
     * Get the time the last IndexItem was put into the Index.
     * @return  ZeroTimestamp if there is no last item, usually when there are no items in the index
     */
    public Timestamp getLastTime() {
	if (isSelection) {
	    try {
		IndexItem last = getItem(getLength()-1);
		return last.getIndexTimestamp();
	    } catch (GetItemException gie) {
		return Timestamp.ZERO;
	    }
	} else {
	    return indexImpl.getLastTime();
	}
    }


    /**
     * Get the data time for the first IndexItem in the Index.
     * @return ZeroTimestamp if there is no first item, usually when there are no items in the index
     */
    public Timestamp getFirstDataTime() {
	if (isSelection) {
	    try {
		IndexItem first = getItem(0);
		return first.getDataTimestamp();
	    } catch (GetItemException gie) {
		return Timestamp.ZERO;
	    }
	} else {
	    return indexImpl.getFirstDataTime();
	}
    }

    /**
     * Get the data time for the last IndexItem in the Index.
     * @return ZeroTimestamp if there is no last item, usually when there are no items in the index
     */
    public Timestamp getLastDataTime() {
	if (isSelection) {
	    try {
		IndexItem last = getItem(getLength()-1);
		return last.getDataTimestamp();
	    } catch (GetItemException gie) {
		return Timestamp.ZERO;
	    }
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
    public long addItem(DataItem item) throws IndexTerminatedException, IndexClosedException, IndexActivationException , AddItemException {
	if (isSelection) {
	    throw new IndexTerminatedException("Can't add data to an Index selection");
	} else {
	    return indexImpl.addItem(item);
	}
    }

    /**
     * Add a Data Item to the Index with a specific Data Timestamp
     */
    public long addItem(DataItem item, Timestamp datatime) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {
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
	    return selectionLength;
	} else {
	    return indexImpl.getLength();
	}
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n) throws GetItemException {
	if (isSelection) {
	    return indexImpl.getItem(n+start.value());
	} else {
	    return indexImpl.getItem(n);
	}
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p) throws GetItemException {
	if (isSelection) {
	    return indexImpl.getItem((Position)new AbsoluteAdjustablePosition(p).adjust(start.value()));
	} else {
	    return indexImpl.getItem(p);
	}
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime) throws GetItemException {
	TimestampMapping tsm = locate(t, sel, lifetime);
	return getItem(tsm.position());
    }

    /**
     * Hollow the IndexItem at the position.
     */
    public boolean hollowItem(long position) {
	if (isSelection) {
	    return indexImpl.hollowItem(position+ start.value());
	} else {
	    return indexImpl.hollowItem(position);
	}
    }

    /**
     * Hollow the IndexItem at the position.
     */
    public boolean hollowItem(Position p) {
	if (isSelection) {
	    return indexImpl.hollowItem((Position)new AbsoluteAdjustablePosition(p).adjust(start.value()));
	} else {
	    return indexImpl.hollowItem(p);
	}
    }
	

    /**
     * Get the  last time an IndexItem was accessed from the index.
     */
    public Timestamp getLastAccessTime() {
	return indexImpl.getLastAccessTime();
    }

    /**
     * Get the path of the index file.
     * @return null if there is no index path
     */
    public String getIndexPathName() {
	return indexImpl.getIndexPathName();
    }

    /**
     * Get the path of the data if the index data style
     * is external or shadow.
     * @return null if there is no data path
     */
    public String getDataPathName() {
	return indexImpl.getDataPathName();
    }

    /**
     * Get the description for an index.
     * @return null if there is no description
     */
    public Description getDescription() {
	return indexImpl.getDescription();
    }

    /**
     * Set the description.
     * This is one of the few attributes of an index that can be set directly.
     */
    public IndexHeader setDescription(Description description) {
	indexImpl.setDescription(description);
	return this;
    }




    /**
     * Does a timestamp fall within the bounds of the Index.
     * The bounds are the first time data is put in and the last
     * time data is put in the Index.
     */
    public boolean contains(Timestamp t, IndexTimestampSelector sel) {
	if (isSelection) {
	    IndexItem first = null;
	    IndexItem last = null;
	    Timestamp firstTS =  null;
	    Timestamp lastTS = null;


	    // if we cant get both the items
	    // then the selection doesn't contain the timestamp
	    // so return false
	    try {
		first = getItem(0);
		last = getItem(getLength()-1);
	    } catch (GetItemException gie) {
		return false;
	    }


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
     * @return null if no position is found
      */
    public TimestampMapping locate(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime) {
	Timestamp firstTS =  null;
	Timestamp lastTS = null;

	if (isSelection) {
	    if (contains(t, sel)) {
		return indexImpl.locate(t, sel, lifetime);
	    } else {
		// now try and determine if it is too low or too high
		IndexItem first = null;
		IndexItem last = null;

		// if we cant get both the items
		// then the selection doesn't contain the timestamp
		// so return null
		try {
		    first = getItem(0);
		    last = getItem(getLength()-1);
		} catch (GetItemException gie) {
		    return null;
		}


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
		    throw new RuntimeException("TimeIndex: locate failed to process timestamp " + t + ". First time = " + firstTS + ". Last time = " + lastTS + ".");
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
		// interval start() and end() return a TimestampMapping
		Position startV = ((TimestampMapping)resolvedInterval.start()).position();
		Position endV = ((TimestampMapping)resolvedInterval.end()).position();


		System.err.println("TimeIndex: select() pre: startV = " + startV + " endV = " + endV);

		if (overlap == Overlap.STRICT) {
		    // if the Interval must have strict over lap with 
		    // the index then complain if the start or the end
		    // position is out of bounds

		    if (startV == Position.TOO_LOW) {
			throw new PositionOutOfBoundsException("Can't select before the start of an Index");
		    }

		    if (endV == Position.TOO_HIGH) {
			throw new PositionOutOfBoundsException("Can't select beyond the end of an Index");
		    }
		} else {
		    // if the Interval can overlap freely with 
		    // the index then do some tweaking if the start or the end
		    // position is out of bounds

		    if (startV == Position.TOO_LOW && endV == Position.TOO_LOW) { // both TOO_LOW
			selectionLength = 0;
		    } else if (startV == Position.TOO_HIGH && endV == Position.TOO_HIGH) { // both TOO_HIGH
			selectionLength = 0;
		    } else if (startV == Position.TOO_LOW && endV == Position.TOO_HIGH) { // start too low, end too high
			startV = new AbsolutePosition(0);
			endV = new AbsolutePosition(getLength()-1);
			selectionLength = endV.value() - startV.value() + 1;
		    } else if (startV == Position.TOO_LOW) { // startV TOO_LOW
			startV = new AbsolutePosition(0);
			selectionLength = endV.value() - startV.value() + 1;
		    } else if (endV == Position.TOO_HIGH) {  // endV TOO_HIGH
			endV = new AbsolutePosition(getLength()-1);
			selectionLength = endV.value() - startV.value() + 1;
		    } else {
			selectionLength = endV.value() - startV.value() + 1;
		    }

		}

		System.err.println("TimeIndex: select() post: startV = " + startV + " endV = " + endV);
		try {
		    TimeIndex selection = (TimeIndex)this.clone();

		    // setup the new selection values
		    selection.selectionInterval = interval;
		    selection.selectionLength = selectionLength;
		    selection.isSelection = true;
		    selection.isTerminated = true;
		    selection.position = new AbsoluteAdjustablePosition(0);
		    selection.mark = null;

		    // setup the start and end values
		    if (startV == Position.TOO_LOW || startV == Position.TOO_HIGH) {
			selection.start = startV;
		    } else {
			selection.start = new AbsolutePosition(start.value() +  startV.value());
		    }

		    if (endV == Position.TOO_LOW || endV == Position.TOO_HIGH) {
			selection.end = endV;
		    } else {
			selection.end = new AbsolutePosition(start.value() + endV.value());
		    }

		    return selection;
		} catch ( CloneNotSupportedException cnse) {
		    // the clone failed
		    throw new RuntimeException("TimeIndex::select().  Failed to clone " + this);
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
     * Is the index still in time order.
     */
    public boolean isInTimeOrder() {
	return indexImpl.isInTimeOrder();
    }

    /**
     * Flush this index.
     */
    public boolean flush() {
	return indexImpl.flush();
    }

    /**
     * Is the Index closed.
     */
    public boolean isClosed() {
	return indexImpl.isClosed();
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
	    // set Position.TOO_HIGH
	    position = Position.TOO_HIGH;
	    return this;
	} else {
	    ((AdjustablePosition)position).adjust(+1);
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
	    // set Position.TOO_LOW
            position = Position.TOO_LOW;
	    return this;
	} else {
	    ((AdjustablePosition)position).adjust(-1);
	    return this;
	}
    }


    /**
     * Get the Index Item from the Index at position position().
     */
    public IndexItem getItem() throws GetItemException {
	if (position == null) {
	    throw new PositionOutOfBoundsException("Position not set. Region does not exist");
	} else {
	    return getItem(position);
	}
    }

    /**
     * Get the Index Item from the Index at position mark().
     */
    public IndexItem getItemAtMark() throws GetItemException {
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
	AdjustablePosition temp = (AdjustablePosition)position;
	position = mark;
	mark = temp;
	return this;
    }

}


