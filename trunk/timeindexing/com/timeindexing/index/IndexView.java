// Index.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.Overlap;
import com.timeindexing.basic.Position;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Lifetime;
import com.timeindexing.data.DataItem;
import com.timeindexing.index.IndexTimestampSelector;

/**
 * An index view has the moethods needed
 * for view on an Index.
 */
public interface IndexView extends Index {
    /**
     * Select an Interval and return an IndexView
     * which is a view on the underlying Index.
     */
    public IndexView select(Interval interval, IndexTimestampSelector sel, Overlap overlap, Lifetime lifetime);

    /**
     * Return the Interval used to get a selection.
     * @return null if the view is not a selection.
     */
    public Interval getSelectionInterval();

    /**
     * Return the IndexView used to get a selection.
     * @return null if the view is not a selection.
     */
    public IndexView getSelectionIndexView();

    /**
     * Is the IndexView a selection.
     */
    public boolean isSelection();

    /**
     * Gets the current navigation position into the IndexView.
     */
    public Position position();

    /**
     * Sets the current navigation position into the IndexView
     * specified as a Position.
     */
    public IndexView position(Position p);

    /**
     * Sets the current navigation position into the IndexView.
     * specified as a long.
     */
    public IndexView position(long n);

    /**
     * Sets the current navigation position into the IndexView.
     * specified as a Timestamp.
     */
    public IndexView position(Timestamp t, IndexTimestampSelector selector, Lifetime lifetime);

    /**
     * Get the start position, in the index, of this IndexView.
     */
    public Position getStartPosition();

    /**
     * Get the end position, in the index, of this IndexView.
     */
    public Position getEndPosition();

    /**
     * Sets the mark into the IndexView, using the current navigation position
     * as the mark value.
     */
    public IndexView mark();

    /**
     * Sets the current navigation position into the IndexView
     * to be one forward.
     */
    public IndexView forward();

    /**
     * Sets the current navigation position into the IndexView
     * to be one backward.
     */
    public IndexView backward();

    /**
     * Get the Index Item from the Index at position position().
     */
    public IndexItem getItem() throws GetItemException;

    /**
     * Get the Index Item from the Index at position mark().
     */
    public IndexItem getItemAtMark() throws GetItemException;

    /**
     * What is the region covered by position and mark.
     * Returned value is an Interval.
     */
    public Interval region();

    /**
     * Exchanges the mark into the IndexView, with the current navigation position.
     */
    public IndexView exchange();

    



}