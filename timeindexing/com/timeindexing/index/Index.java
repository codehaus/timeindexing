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
// Index.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.Overlap;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.AbsoluteTimestamp;
import com.timeindexing.time.Lifetime;
import com.timeindexing.time.IntervalSpecifier;
import com.timeindexing.data.DataItem;
import com.timeindexing.util.DoubleLinkedList;
import com.timeindexing.cache.CachePolicy;

import java.util.Iterator;

/**
 * An index.
 * This is the generic interface to an index.
 * It has all the methods for the header and for all the data requests.
 */
public interface Index extends IndexHeader {
    /**
     * Get the size of the index items.
     */
    public int getItemSize();

    /**
     * Get the type name of the things in the data stream.
     */
    public String getDataType(ID typeID);

    /**
     * Does this index have a typed name.
     */
    public boolean hasDataType(String typeName);

    /**
     * Add a new data type
     * @return true, if a new type was added; false, if the index had this ID/typeName pair already
     */
    public boolean addDataType(ID typeID, String typeName);


    /**
     * Does this index have annotations.
     */
    public boolean hasAnnotations();

    /**
     * Get the annotation style.
     * Either inline or external.
     */
    public int getAnnotationStyle();

    /**
     * Update the description of this index.
     */
    public Index updateDescription(Description description);

    /**
     * Add a Data Item to the Index.
     */
    public IndexItem addItem(DataItem item) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException ;

    /**
     * Add a Data Item to the Index with a speicifed Data Timestamp
     */
    public IndexItem addItem(DataItem item, Timestamp dataTime) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException;

    /**
     * Add a Data Item to the Index with a speicifed Data Timestamp and some annotation data
     */
    public IndexItem addItem(DataItem item, Timestamp dataTime, long annotation) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException;

    /**
     * Add a Reference to an IndexItem in a Index.
     * The Data Timestamp of the IndexItem is passed into this Index.
     */
    public IndexItem addReference(IndexItem item, Index other) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException ;

    /**
     * Add a Reference to an IndexItem in a Index.
     * The Data Timestamp of the IndexItem is the one specified.
     */
    public IndexItem addReference(IndexItem item, Index other, Timestamp dataTime) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException;

    /**
     * Add a Reference to an IndexItem in a Index.
     * The Data Timestamp of the IndexItem is the one specified, as is the annotation value.
     */
    public IndexItem addReference(IndexItem item, Index other, Timestamp dataTime, long annotation) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException;

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n) throws GetItemException, IndexClosedException;

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p) throws GetItemException, IndexClosedException;

    /**
     * Get an Index Item from the Index.
     * Uses IndexTimestampSelector.DATA and Lifetime.CONTINUOUS as defaults.
     */
    public IndexItem getItem(Timestamp t) throws GetItemException, IndexClosedException;

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime) throws GetItemException, IndexClosedException;


    /**
     * Does a timestamp fall within the bounds of the Index.
     * Uses IndexTimestampSelector.DATA as a default.
     * The bounds are the first time data is put in and the last
     * time data is put in the Index.
     */
    public boolean contains(Timestamp t);

    /**
     * Does a timestamp fall within the bounds of the Index.
     * The bounds are the first time data is put in and the last
     * time data is put in the Index.
     */
    public boolean contains(Timestamp t, IndexTimestampSelector sel);

    /**
     * Try and determine the position associated with the speicifed Timestamp.
     * Uses IndexTimestampSelector.DATA and Lifetime.CONTINUOUS as defaults.
     * Returns a TimestampMapping which contains the original time
     * and the found position.
     */
    public TimestampMapping locate(Timestamp t);

    /**
     * Try and determine the position associated
     * with the speicifed Timestamp.
     * Returns a TimestampMapping which contains the original time
     * and the found position.
     */
    public TimestampMapping locate(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime);

    /**
     * Try and determine the Timestamp associated with the speicifed Position.
     * Uses IndexTimestampSelector.DATA and Lifetime.CONTINUOUS as defaults.
     * Returns a TimestampMapping which contains the original Position
     * and the found Timestamp.
     */
    public TimestampMapping locate(Position p);

    /**
     * Try and determine the Timestamp associated
     * with the speicifed Position.
     * Returns a TimestampMapping which contains the original Position
     * and the found Timestamp.
     */
    public TimestampMapping locate(Position p, IndexTimestampSelector sel, Lifetime lifetime);


    /**
     * Select an Interval given an Interval object.
     * Defaults to using IndexTimestampSelector.DATA, Overlap.FREE, 
     * and Lifetime.CONTINUOUS, as these are the most common values.
     * Returns null if it cant be done.
     */
    public IndexView select(Interval interval);

    /**
     * Select an Interval given a Timestamp and an IntervalSpecifier.
     * Defaults to using IndexTimestampSelector.DATA, Overlap.FREE, 
     * and Lifetime.CONTINUOUS, as these are the most common values.
     * Returns null if it cant be done.
     */
    public IndexView select(AbsoluteTimestamp t, IntervalSpecifier intervalSpecifier);

   /**
     * Select an Interval given an Interval object.
     * Returns null if it cant be done.
     */
    public IndexView select(Interval interval, IndexTimestampSelector selector, Overlap overlap, Lifetime lifetime);

    /**
     * Select an Interval given a Timestamp and an IntervalSpecifier.
     */
    public IndexView select(AbsoluteTimestamp t, IntervalSpecifier intervalSpecifier, IndexTimestampSelector selector, Overlap overlap, Lifetime lifetime);


    /**
     * Filter some IndexItems out into a new IncoreIndex.
     */
    public IndexView filter(Function f) throws TimeIndexException;

    /**
     * Map a function to all of the IndexItems, resulting  in a new IncoreIndex.
     */
    public IndexView map(Function f) throws TimeIndexException;

    /**
     * Apply a function to all of the IndexItems, resulting  in a List of results.
     */
    public DoubleLinkedList apply(Function fn) throws TimeIndexException;

    /**
     * Get the  last time an IndexItem was accessed from the index.
     */
    public Timestamp getLastAccessTime();


    /**
     * Is the Index activated.
     */
    public boolean isActivated();

    /**
     * Make the Index activated.
     * It is not possible to add items to an Index
     * that is not active.
     */
    public Index activate() throws IndexReadOnlyException, IndexWriteLockedException;

    /**
     * Make the Index finalized.
     * It is not possible to add items ever again
     * to an Index that has been terminated.
     */
    public Index terminate();

    /**
     * Commit all changes to the index.
     * Intened to sync all associated streams and files,
     * and this sets the end time too.
     */
    public boolean commit() throws IndexCommitException;

    /**
     * Set auto commit to be true or false.
     * When auto commit is true, then every addItem() is
     * automatically committed.
     * @return the previous value of auto commit.
     */
    public boolean setAutoCommit(boolean commit);

    /**
     * Is the Index closed.
     */
    public boolean isClosed();

    /**
     * Close the index.
     * Intened to close all associated streams and files,
     * and this sets the end time too.
     */
    public boolean close() throws IndexCloseException;

    /**
     * Has the index changed in any way.
     */
    public boolean isChanged();

    /**
     * Has the Index been write-locked.
     */
    public boolean isWriteLocked();

    /**
     * Get an iterator over the IndexItems in the Index.
     */
    public Iterator iterator();

    /**
     * Get a view onto the Index.
     */
    public IndexView asView();

    /**
     * Get the CachePolicy 
     */
    public CachePolicy getCachePolicy(); 


    /**
     * Set a CachePolicy in order to manage the cache.
     * Setting a new CachePolicy in the middle of operation
     * can lose some timing information held by the existing CachePolicy,
     * so use with care.
     * @return the old policy if the policy was set
      */
    public CachePolicy setCachePolicy(CachePolicy policy); 

    /**
     * Does the index load data automatically when doing a get item. 
     */
    public boolean getLoadDataAutomatically();

    /**
     * Load data automatically when doing a get item.
     * @return the previous value of this status
     */
    public boolean setLoadDataAutomatically(boolean load);


}



