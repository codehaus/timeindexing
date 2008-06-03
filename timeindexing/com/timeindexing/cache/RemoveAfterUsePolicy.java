/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



// RemoveAfterUsePolicy.java

package com.timeindexing.cache;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.util.DoubleLinkedList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Remove items after they have been used.
 */
public class RemoveAfterUsePolicy extends AbstractCachePolicy implements CachePolicy {
    // how many to queue before removeing
    int queueWindow = 0;

    /**
     * Construct this policy object
     */
    public RemoveAfterUsePolicy() {
	monitorList = new DoubleLinkedList(); //new IndexItemComparator());
	queueWindow = 40;
    }

    /**
     * Construct this policy object
     */
    public RemoveAfterUsePolicy(int winSize) {
	monitorList = new DoubleLinkedList(); // new IndexItemComparator());
	queueWindow = winSize;
    }

    /**
     * Called at the beginning of cache.addItem()
     * @param item the item being added
     * @param pos the position the item is being added to
     */
    public Object notifyAddItemBegin(IndexItem item,long pos) {
	//System.err.print("notifyAddItemBegin: ");
	notifyGetItemBegin(item, pos);
	return null;
    }

    /**
     * Called at the beginning of cache.addItem()
     * @param item the item being added
     * @param pos the position the item is being added to
     */
    public Object notifyAddItemEnd(IndexItem item, long pos) {
	//System.err.print("notifyAddItemEnd: ");
	notifyGetItemEnd(item, pos);
	return null;
    }    

    /**
     * Called at the beginning of cache.getItem()
     * @param pos the position being requested
     */
    public Object notifyGetItemBegin(IndexItem item, long pos) {
	// if the item is in the monitorList remove it.
	monitorList.remove(item);
	    //System.err.println("DeQueue " + item.getPosition() + ".Remove list size = " + monitorList.size());

	// if there's something to delete
	// remove one item
	while (monitorList.size() >= queueWindow) {
	    ManagedIndexItem first = (ManagedIndexItem)monitorList.getFirst();
	    //System.err.print("Removeing " + first.getPosition() + ". Last accesse time: " + first.getLastAccessTime() + ".Remove list size = " + monitorList.size());
	    monitorList.remove(first);
	    
	    cache.removeItem(first.getPosition());
	}

	return null;
    }

    /**
     * Called at the end of cache.getItem()
     * @param item the item being returned
     */
    public Object notifyGetItemEnd(IndexItem item, long pos) {
	monitorList.add(item);
	//System.err.println("Queuing " + item.getPosition() + ".Remove list size = " + monitorList.size());

	return null;
    }
	    

    /**
     * TO String
     */
    public String toString() {
	return "RemoveAfterUsePolicy: queueWindow = " + monitorList.size() + "/" + queueWindow;
    }
}
