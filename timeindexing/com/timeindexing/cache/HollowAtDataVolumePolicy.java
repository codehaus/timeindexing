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
// HollowAtDataVolumePolicy.java

package com.timeindexing.cache;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.util.DoubleLinkedList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Hollow items after the amount of held data
 * reaches a certain volume
 */
public class HollowAtDataVolumePolicy extends AbstractCachePolicy implements CachePolicy {
    // The volume to hold before removing
    long volumeThreshold = 0;

    /**
     * Construct this policy object
     */
    public HollowAtDataVolumePolicy() {
	monitorList = new DoubleLinkedList();
	// 50k
	volumeThreshold = 50 * 1024;
    }

    /**
     * Construct this policy object
     */
    public HollowAtDataVolumePolicy(long volume) {
	monitorList = new DoubleLinkedList();
	volumeThreshold = volume;
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

	// if the first item in the monitorList
	// was last accessed with an elapsed time greater
	// than the timeout, then remove it
	// remove one item
	if (monitorList.size() > 0) {

 	    while (cache.getDataVolume() > volumeThreshold) {
		ManagedIndexItem first = (ManagedIndexItem)monitorList.getFirst();

		if (cache.getDataVolume() > volumeThreshold) {
		    //System.err.print("Hollowing " + first.getPosition() + ". Last accesse time: " + first.getLastAccessTime());
		    monitorList.remove(first);
    
		    cache.hollowItem(first.getPosition());
		}
	    }
	}

	return null;
    }

    /**
     * Called at the end of cache.getItem()
     * @param item the item being returned
     */
    public Object notifyGetItemEnd(IndexItem item, long pos) {
	monitorList.add(item);
	//System.err.println("Queuing " + item.getPosition() + ".Hollow list size = " + monitorList.size());

	return null;
    }
	    

    /**
     * TO String
     */
    public String toString() {
	return "HollowAtDataVolumePolicy: queueWindow = " + monitorList.size() + "/" + volumeThreshold;
    }
}
