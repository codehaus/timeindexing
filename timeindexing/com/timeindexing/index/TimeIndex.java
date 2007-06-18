// TimeIndex.java

package com.timeindexing.index;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimeSpecifier;
import com.timeindexing.time.AbsoluteTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Lifetime;
import com.timeindexing.time.IntervalSpecifier;
import com.timeindexing.time.Clock;
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
import com.timeindexing.cache.CachePolicy;

import java.util.Iterator;
import java.net.URI;

/**
 * This is the generic object that applications interact with.
 * It is a view onto an index.
 */
public  class TimeIndex implements Index, IndexView,  Cloneable, java.io.Serializable {
    /**
     * The actual implementation of an index.
     */
    Index indexModel = null;

    /*
     * The start position. By default this is the zeroth elemnt
     */
    Position start = null;
     
     /*
     * The end position. By default this is last element.
     */
    Position end = null;

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
     * An Interval for the selection.
     * This is a reference to the Interval that was used
     * to make a selection.
     */
    Interval selectionInterval = null;

    /**
     * The length of a selection
     */
    long selectionLength = 0;

    /**
     * The IndexView that this view is a selection of.
     */
    IndexView selectionIndexView = null;

    /*
     * Is this terminated.
     * This variable is only used when a selection is taken.
     */
    public boolean isTerminated = false;

   /**
     * Construct a Time Index using the nominated Index object.
     */
    protected TimeIndex(Index impl) {
	indexModel = impl;

	// set up the start position of this view 
	start = new AbsolutePosition(0);

	// set up the end position of this view 
	if (indexModel.getLength() == 0) {
	    end = new AbsolutePosition(0);
	} else {
	    end = new AbsolutePosition(indexModel.getLength()-1);
	}

	// set up the current navigation position of this view 
	position = new AbsoluteAdjustablePosition(0);
    }

    /**
     * The name of the index.
     */
    public String getName() {
	return indexModel.getName();
    }

    /**
     * Get an ID of an index.
     */
    public ID getID() {
	return indexModel.getID();
    }

   /**
     * Get the Index specification in the form of a URI.
     */
    public URI getURI() {
	return indexModel.getURI();
    }

    /**
     * Get the type of an index.
     */
    public IndexType getIndexType() {
	return indexModel.getIndexType();
    }

   /**
     * Get the index data type.
     * Some indexes have the same type throughout,
     * other have mixed type data.
     */
    public DataType getIndexDataType() {
	return indexModel.getIndexDataType();
    }

     /**
      * Get the start time of the index.
      * This is when the index was created not necessarliy when the first item
      * was added to the index.
      */
    public Timestamp getStartTime() {
	return indexModel.getStartTime();
    }

     /**
      * Get the end time of the index.
      * This is the time the last item was closed, not necessarliy when the last item
      * was added to the index.
      */
    public Timestamp getEndTime() {
	return indexModel.getEndTime();
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
	    } catch (IndexClosedException ice) {
		return Timestamp.ZERO;
	    } catch (PositionOutOfBoundsException poobe) {
		return Timestamp.ZERO;
	    }
	} else {
	    return indexModel.getFirstTime();
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
	    } catch (IndexClosedException ice) {
		return Timestamp.ZERO;
	    } catch (PositionOutOfBoundsException poobe) {
		return Timestamp.ZERO;
	    }
	} else {
	    return indexModel.getLastTime();
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
	    } catch (IndexClosedException ice) {
		return Timestamp.ZERO;
	    } catch (PositionOutOfBoundsException poobe) {
		return Timestamp.ZERO;
	    }
	} else {
	    return indexModel.getFirstDataTime();
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
	    } catch (IndexClosedException ice) {
		return Timestamp.ZERO;
	    } catch (PositionOutOfBoundsException poobe) {
		return Timestamp.ZERO;
	    }
	} else {
	    return indexModel.getLastDataTime();
	}
    }

    /**
     * Get the size of the index items.
     */
    public int getItemSize() {
	return indexModel.getItemSize();
    }


    /**
     * Does the index have fixed size data.
     */
    public boolean isFixedSizeData() {
	return indexModel.isFixedSizeData();
    }


    /**
     * Get the size of the data items, if there is fixed size data.
     */
    public long getDataSize() {
	return indexModel.getDataSize();
    }


    /**
     * Get the type name of the things in the data stream.
     */
    public String getDataType(ID typeID) {
	return indexModel.getDataType(typeID);
    }

    /**
     * Does this index have a typed name.
     */
    public boolean hasDataType(String typeName) {
	return indexModel.hasDataType(typeName);
    }


    /**
     * Add a new data type
     * @return true, if a new type was added; false, if the index had this ID/typeName pair already
     */
    public boolean addDataType(ID typeID, String typeName) {
	return indexModel.addDataType(typeID, typeName);
    }


    /**
     * Does this index have annotations.
     */
    public boolean hasAnnotations() {
	return indexModel.hasAnnotations();
    }

    /**
     * Get the annotation style.
     * Either inline or external.
     */
    public int getAnnotationStyle() {
	return indexModel.getAnnotationStyle();
    }

    /**
     * Add a Data Item to the Index.
     */
    public IndexItem addItem(DataItem item) throws IndexTerminatedException, IndexClosedException, IndexActivationException , AddItemException {
	if (isSelection) {
	    throw new IndexTerminatedException("Can't add data to an Index selection");
	} else {
	    return indexModel.addItem(item);
	}
    }

    /**
     * Add a Data Item to the Index with a specific Data Timestamp
     */
    public IndexItem addItem(DataItem item, Timestamp datatime) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {
	if (isSelection) {
	    throw new IndexTerminatedException("Can't add data to an Index selection");
	} else {
	    return indexModel.addItem(item, datatime);
	}
    }

    /**
     * Add a Data Item to the Index with a speicifed Data Timestamp and some annotation data
     */
    public IndexItem addItem(DataItem item, Timestamp datatime, long annotation) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {
	if (isSelection) {
	    throw new IndexTerminatedException("Can't add data to an Index selection");
	} else {
	    return indexModel.addItem(item, datatime, annotation);
	}
    }



    /**
     * Add a Referemnce to an IndexItem in a Index.
     */
    public IndexItem addReference(IndexItem otherItem, Index otherIndex) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {
	if (isSelection) {
	    throw new IndexTerminatedException("Can't add data to an Index selection");
	} else {
	    return indexModel.addReference(otherItem, otherIndex);
	}
    }

    /**
     * Add a Referemnce to an IndexItem in a Index.
     */
    public IndexItem addReference(IndexItem otherItem, Index otherIndex, Timestamp dataTS) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {
	if (isSelection) {
	    throw new IndexTerminatedException("Can't add data to an Index selection");
	} else {
	    return indexModel.addReference(otherItem, otherIndex, dataTS);
	}
    }

    /**
     * Add a Reference to an IndexItem in a Index.
     * The Data Timestamp of the IndexItem is the one specified, as is the annotation value.
     */
    public IndexItem addReference(IndexItem otherItem, Index otherIndex, Timestamp dataTS, long annotation) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {
	if (isSelection) {
	    throw new IndexTerminatedException("Can't add data to an Index selection");
	} else {
	    return indexModel.addReference(otherItem, otherIndex, dataTS, annotation);
	}
    }


    /**
     * Get the no of items in the index.
     */
    public long getLength() {
	if (isSelection) {
	    return selectionLength;
	} else {
	    return indexModel.getLength();
	}
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n) throws GetItemException, IndexClosedException {
	if (isSelection) {
	    return indexModel.getItem(n+start.value());
	} else {
	    return indexModel.getItem(n);
	}
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p) throws GetItemException, IndexClosedException {
	if (isSelection) {
	    return indexModel.getItem((Position)new AbsoluteAdjustablePosition(p).adjust(start));
	} else {
	    return indexModel.getItem(p);
	}
    }

    /**
     * Get an Index Item from the Index.
     * Uses IndexTimestampSelector.DATA and Lifetime.CONTINUOUS as defaults.
     */
    public IndexItem getItem(Timestamp t) throws GetItemException, IndexClosedException {
	return getItem(t, IndexTimestampSelector.DATA, Lifetime.CONTINUOUS);
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime) throws GetItemException, IndexClosedException {
	//return indexModel.getItem(t, sel, lifetime);
	TimestampMapping tsm = locate(t, sel, lifetime);
	return getItem(tsm.position());
    }

    /**
     * Get the  last time an IndexItem was accessed from the index.
     */
    public Timestamp getLastAccessTime() {
	return indexModel.getLastAccessTime();
    }

    /**
     * Get the path of the index file.
     * @return null if there is no index path
     */
    public String getIndexPathName() {
	return indexModel.getIndexPathName();
    }

    /**
     * Get the path of the data if the index data style
     * is external or shadow.
     * @return null if there is no data path
     */
    public String getDataPathName() {
	return indexModel.getDataPathName();
    }

    /**
     * Get the description for an index.
     * @return null if there is no description
     */
    public Description getDescription() {
	return indexModel.getDescription();
    }

    /**
     * Set the description.
     * This is one of the few attributes of an index that can be set directly.
     */
    public Index updateDescription(Description description) {
	indexModel.updateDescription(description);
	return this;
    }

    /**
     * Does a timestamp fall within the bounds of the Index.
     * Uses IndexTimestampSelector.DATA as a default.
     * The bounds are the first time data is put in and the last
     * time data is put in the Index.
     */
    public boolean contains(Timestamp t) {
	return contains(t, IndexTimestampSelector.DATA);
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
	    } catch (IndexClosedException ice) {
		return false;
	    } catch (PositionOutOfBoundsException poobe) {
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
	    return indexModel.contains(t, sel);
	}
    }

    /**
     * Try and determine the position associated with the speicifed Timestamp.
     * Uses IndexTimestampSelector.DATA and Lifetime.CONTINUOUS as defaults.
     * Returns a TimestampMapping which contains the original time
     * and the found position.
     */
    public TimestampMapping locate(Timestamp t) {
	return locate(t, IndexTimestampSelector.DATA, Lifetime.CONTINUOUS);
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
		// locate in underlying index
		TimestampMapping underlyingMapping = indexModel.locate(t, sel, lifetime);
		// now map the position into one in this selection
		Position selectionPosition = (Position)new AbsoluteAdjustablePosition(underlyingMapping.position()).adjust(- (start.value()));
		return new TimestampMapping(underlyingMapping.timestamp(), selectionPosition);
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
		} catch (IndexClosedException ice) {
		    return null;
		} catch (PositionOutOfBoundsException poobe) {
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
	    return indexModel.locate(t, sel, lifetime);
	}
    }

    /**
     * Try and determine the Timestamp associated with the speicifed Position.
     * Uses IndexTimestampSelector.DATA and Lifetime.CONTINUOUS as defaults.
     * Returns a TimestampMapping which contains the original Position
     * and the found Timestamp.
     */
    public TimestampMapping locate(Position p) {
	return locate(p, IndexTimestampSelector.DATA, Lifetime.CONTINUOUS);
    }


    /**
     * Try and determine the Timestamp associated
     * with the speicifed Position.
     * Returns a TimestampMapping which contains the original Position
     * and the found Timestamp.
     * Lifetime has no effect in this situation.
     */
    public TimestampMapping locate(Position p, IndexTimestampSelector sel, Lifetime lifetime) {
	if (isSelection) {
	    // map selection position into underlying position
	    Position newStartPos = (Position)new AbsoluteAdjustablePosition(p).adjust(start);
	    // locate in underlying index
	    TimestampMapping underlyingMapping = indexModel.locate(newStartPos, sel, lifetime);
	    // now map the underlying position into one in this selection
	    Position selectionPosition = (Position)new AbsoluteAdjustablePosition(underlyingMapping.position()).adjust(- (start.value()));
	    // ASSERT: selectionPosition == p
	    return new TimestampMapping(underlyingMapping.timestamp(), selectionPosition);
	    
	} else {
	    return indexModel.locate(p, sel, lifetime);
	}
    }


    /**
     * Select an Interval given an Interval object.
     * Defaults to using IndexTimestampSelector.DATA, Overlap.FREE, 
     * and Lifetime.CONTINUOUS, as these are the most common values.
     * Returns null if it cant be done.
     */
    public IndexView select(Interval interval) {
	// ensure we call this class's select()
	// to get the correct processinf if isSelection == true
	return select(interval, IndexTimestampSelector.DATA, Overlap.FREE, Lifetime.CONTINUOUS);
    }


    /**
     * Select an Interval given a Timestamp and an IntervalSpecifier.
     * Defaults to using IndexTimestampSelector.DATA, Overlap.FREE, 
     * and Lifetime.CONTINUOUS, as these are the most common values.
     * Returns null if it cant be done.
     */
    public IndexView select(AbsoluteTimestamp t, IntervalSpecifier intervalSpecifier) {
	// ensure we call this class's select()
	// to get the correct processinf if isSelection == true
	Interval interval = intervalSpecifier.instantiate(t);
	return select(interval, IndexTimestampSelector.DATA, Overlap.FREE, Lifetime.CONTINUOUS);
    }

    /**
     * Select an Interval.
     * Returns null if it cant be done.
     */
    public IndexView select(Interval interval, IndexTimestampSelector selector, Overlap overlap, Lifetime lifetime) {
	if (isSelection) {
	    // get the actual selection
	    TimeIndex selection = (TimeIndex)indexModel.select(interval, selector, overlap, lifetime);

	    // setup the new selection values
	    selection.selectionIndexView = this;
	    selection.position = new AbsoluteAdjustablePosition(0);

	    // setup the start and end values
	    if (selection.start == Position.TOO_LOW || selection.start == Position.TOO_HIGH) {
		// selection start is out of bounds
		return null;
	    } else {
		// realign start to reflect existing view
		long startValue = selection.start.value();
		selection.start = new AbsolutePosition(start.value() +  startValue);
	    }

	    if (selection.end == Position.TOO_LOW || selection.end == Position.TOO_HIGH) {
		// selection end is out of bounds
		return null;
	    } else {
		// realign end to reflect existing view
		long endValue = selection.end.value();
		selection.end = new AbsolutePosition(start.value() + endValue);
	    }

	    return selection;
	} else {
	    TimeIndex selection = (TimeIndex)indexModel.select(interval, selector, overlap, lifetime);

	    if (selection != null) {
		selection.selectionIndexView = this;
		return selection;
	    } else {
		return null;
	    }	    
	}
    }

    /*
     * Select an Interval given a Timestamp and an IntervalSpecifier.
     */
    public IndexView select(AbsoluteTimestamp t, IntervalSpecifier intervalSpecifier, IndexTimestampSelector selector, Overlap overlap, Lifetime lifetime) {
	// ensure we call this class's select()
	// to get the correct processinf if isSelection == true
 	Interval interval = intervalSpecifier.instantiate(t);
	return select(interval, selector, overlap, lifetime);
    }

    /**
     * Return the Interval used to get a selection.
     * @return null if the view is not a selection.
     */
    public Interval getSelectionInterval() {
	return selectionInterval;
    }

    /**
     * Return the IndexView used to get a selection.
     * @return null if the view is not a selection.
     */
    public IndexView getSelectionIndexView() {
	return selectionIndexView;
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
	return indexModel.isActivated();
    }

    /**
     * Make the Index activated.
     * It is not possible to add items to an Index
     * that is not active.
     */
    public Index activate() throws IndexReadOnlyException, IndexWriteLockedException {
	return indexModel.activate();
    }

    /**
     * Is the Index terminated.
     */
    public boolean isTerminated() {
	return indexModel.isTerminated();
    }

    /**
     * Make the Index finalized.
     * It is not possible to add items ever again
     * to an Index that has been terminated.
     */
    public Index terminate() {
	return indexModel.terminate();
    }


    /**
     * Is the index still in time order.
     */
    public boolean isInTimeOrder() {
	return indexModel.isInTimeOrder();
    }

    /**
     * Commit all changes to the index.
     */
    public boolean commit() throws IndexCommitException {
	return indexModel.commit();
    }

    /**
     * Set auto commit to be true or false.
     * When auto commit is true, then every addItem() is
     * automatically committed.
     * @return the previous value of auto commit.
     */
    public boolean setAutoCommit(boolean commit) {
	return indexModel.setAutoCommit(commit);
    }

    /**
     * Is the Index closed.
     */
    public boolean isClosed() {
	return indexModel.isClosed();
    }

    /**
     * Close the index.
     * Intened to close all associated streams and files,
     * and sets the end time too, if this is the last view to close.
     * <b>This also disconnects the view from the model.</b>
     * Any methods called after this will fail.
     */
    public boolean close() throws IndexCloseException {
	return indexModel.close();
    }

    /**
     *  Has the index changed in any way.
     */
    public boolean isChanged() {
	return indexModel.isChanged();
    }

    
    /**
     * Is the Index only available for read-only operations.
     */
    public boolean isReadOnly() {
	return indexModel.isReadOnly();
    }

    /**
     * Has the Index been write-locked.
     */
    public boolean isWriteLocked() {
	return indexModel.isWriteLocked();
    }

    /**
     * Get a view onto the Index.
     */
    public IndexView asView() {
	//System.err.println(Clock.time.time() + " " + getURI() + ". AS_VIEW" + ". Thread " + Thread.currentThread().getName() );

	return indexModel.asView();
    }

    /**
     * Get an iterator over the IndexItems in the Index.
     */
    public Iterator iterator() {
	return new IndexIterator(this);
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
     * Sets the current navigation position into the IndexView
     * specified as a Position.
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
     * specified as a long.
     */
    public IndexView position(long n) {
	return position(new AbsoluteAdjustablePosition(n));
    }


    /**
     * Sets the current navigation position into the IndexView
     * specified as a Timestamp.
     * Uses IndexTimestampSelector.DATA and Lifetime.CONTINUOUS as defaults.
     */
    public IndexView position(Timestamp t) {
	return position(t, IndexTimestampSelector.DATA, Lifetime.CONTINUOUS);
    }

    /**
     * Sets the current navigation position into the IndexView
     * specified as a Timestamp.
     */
    public IndexView position(Timestamp t, IndexTimestampSelector selector, Lifetime lifetime) {
	TimestampMapping tsMapping = locate(t, selector, lifetime);

	Position pos = tsMapping.position();

	if (pos == Position.TOO_LOW) {
	    System.err.println("TimeIndex: position located = " + pos);
	    position = new AbsolutePosition(0);
	    return this;
	} else if (pos == Position.TOO_HIGH) {
	    System.err.println("TimeIndex: position located = " + pos);
	    position = new AbsolutePosition(end.value() - start.value());
	    return this;
	} else {
	    position = pos;
	    return this;
	}
    }

    /**
     * Get the start position, in the index, of this IndexView.
     */
    public Position getStartPosition() {
	return start;
    }

    /**
     * Get the end position, in the index, of this IndexView.
     */
    public Position getEndPosition() {
	return end;
    }

    /**
     * Move the current navigation position in the IndexView
     * using the TimeSpecifier.
     * e.g. index.move(new Minutes(10, FORWARD));
     * Uses IndexTimestampSelector.DATA and Lifetime.CONTINUOUS as defaults.
     */
    public IndexView move(TimeSpecifier ts) {
	return move(ts, IndexTimestampSelector.DATA, Lifetime.CONTINUOUS);
    }

    /**
     * Move the current navigation position in the IndexView
     * using the TimeSpecifier.
     * e.g. index.move(new Minutes(10, FORWARD), IndexTimestampSelector.DATA, Lifetime.CONTINUOUS);
     */
    public IndexView move(TimeSpecifier ts, IndexTimestampSelector selector, Lifetime lifetime) {
	// get the current position
	TimestampMapping current = locate(position());

	// work out what the new time would be
	Timestamp newTimestamp = ts.instantiate(current.timestamp());

	// now set the position based on this new time
	IndexView indexView = position(newTimestamp, selector, lifetime);

	return indexView;
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
    public IndexItem getItem() throws GetItemException, IndexClosedException {
	if (position == null) {
	    throw new PositionOutOfBoundsException("Position not set. Region does not exist");
	} else {
	    return getItem(position);
	}
    }

    /**
     * Get the Index Item from the Index at position mark().
     */
    public IndexItem getItemAtMark() throws GetItemException, IndexClosedException {
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
	    return new EndPointInterval((AbsolutePosition)mark, (AbsolutePosition)position);
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

    /**
     * Get the CachePolicy 
     */
    public CachePolicy getCachePolicy() {
	return indexModel.getCachePolicy();
    }

    /**
     * Set a CachePolicy in order to manage the cache.
     * Setting a new CachePolicy in the middle of operation
     * can lose some timing information held by the existing CachePolicy,
     * so use with care.
     * @return the old policy if the policy was set
     */
    public CachePolicy setCachePolicy(CachePolicy policy) {
	return indexModel.setCachePolicy(policy);
    }

    /**
     * Does the index load data automatically when doing a get item. 
     */
    public boolean getLoadDataAutomatically() {
	return indexModel.getLoadDataAutomatically();
    }

    /**
     * Load data automatically when doing a get item.
     * @return the previous value of this status
     */
    public boolean setLoadDataAutomatically(boolean load) {
	return indexModel.setLoadDataAutomatically(load);
    }

}


