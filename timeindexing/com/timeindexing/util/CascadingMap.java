// CascadingMap.java

package com.timeindexing.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
/**
 * A cascading map is a map that holds its own map data,
 * and also references a submap which also holds map data.
 * A cascade of cascading maps can hold more than one version of a key
 * in the individual maps.
 * Any key in a map will supercede the same key in a submap.
 * Keys will be found by traversing as many submaps as is necessary.
 * The first one found will end the traversal.
 * <p>
 * Most of the methods of Map only work
 * on this CascadingMap, they do not operate on submaps.
 * Submaps appear to be read-only.
 */
public class CascadingMap extends HashMap implements Map {
    /**
     * A submap
     */
    CascadingMap submap = null;

    /**
     * A constructor.
     */
    public CascadingMap() {
    }

    /**
     * A constructor which specifies a submap
     * @param aSubMap an existing CascadingMap which will be a submap
     * to this CascadingMap
     */
    public CascadingMap(CascadingMap aSubMap) {
	submap = aSubMap;
    }

    /**
     * Does the casacading map contain a key.
     */
    public boolean containsKey(Object key) {
	if (super.containsKey(key)) {
	    // if the key is in this HashMap
	    return true;
	} else {
	    // look in a submap, if we have one
	    if (submap != null) {
		return submap.containsKey(key);
	    } else {
		return false;
	    }
	}
    }

    /**
     * Get a value from the casacading map for a key.
     */
    public Object get(Object key) {
	if (super.containsKey(key)) {
	    // if the key is in this HashMap
	    return super.get(key);
	} else {
	    // look in a submap, if we have one
	    if (submap != null) {
		return submap.get(key);
	    } else {
		return null;
	    }
	}
    }

    /**
     * Get all the keys in the casacading map.
     */
    public Set keySet() {
	Set keys = new HashSet(super.keySet());

	if (submap != null) {
	    Iterator subkeys = submap.keySet().iterator();
	    while (subkeys.hasNext()) {
		Object nextKey = subkeys.next();

		// only add keys not already present
		if (!keys.contains(nextKey)) {
		    keys.add(nextKey);
		}
	    }
	    return keys;
	} else {
	    return keys;
	}
    }

    /**
     * Get the submap from this CascadingMap.
     */
    public CascadingMap getSubmap() {
	return submap;
    }
}
