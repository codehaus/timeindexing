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
// Grouper.java

package com.timeindexing.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class represents a collection that has been grouped
 * using a criteria specified as a Grouping object.
 * The method getGroup() of Grouping returns an object 
 * which acts as the identifier for a group.
 * Every object in the source collection which has the same value
 * returned by getGroup() will be in the same group.
 * <p>
 * A Grouper is a kind of Map and can therefore be used
 * in any context that accepts a Map.
 * <p>
 * As an example, consider taking a set of Path objects.
 * The following will build a Grouper based on the length
 * of the paths.
 * <pre>
 *    Grouper grouper = new Grouper(pathSet, new Grouping() {
 *	        public Object getGroup(Object obj) {
 *		    Path path = (Path)obj;
 *		    return new Integer(path.length());
 *		}
 *	    });
 * </pre>
 * <p>
 * The resulting Grouper will have keys which are the lengths of
 * the paths, and the values will be a list of paths that have that 
 * length.
 */
public class Grouper extends HashMap implements Map {
    /**
     * This constructor takes a collection and 
     * a Grouping object and creates a grouper.
     */
    public Grouper(Collection collection, Grouping grouping) {
	doGrouping(collection, grouping);
    }

    /**
     * This constructor takes a grouper and 
     * a Grouping object and creates a sub grouper
     * for each existing group in the grouper argumrnt.
     */
    public Grouper(Grouper grouper, Grouping grouping) {
	putAll(grouper);
	groupAGrouper(grouping);
    }


    /**
     * Only used internally.
     */
    protected Grouper() {
    }

    /**
     * Actually do the grouping.
     */
    public boolean doGrouping(Collection collection, Grouping grouping) {
	Iterator collIterator = collection.iterator();

	while (collIterator.hasNext()) {
	    Object collNext = collIterator.next();

	    Object groupSpecifier = grouping.getGroup(collNext);

	    // if groupSpecifier in keys add to values
	    // else make new key and then add to values
	    if (containsKey(groupSpecifier)) {
		add(groupSpecifier, collNext);
	    } else {
		put(groupSpecifier, null);
		add(groupSpecifier, collNext);
	    }
	}
	return true;
    }

    /**
     * This takes a grouper and 
     * a Grouping object and creates a sub grouper
     * for each existing group in the grouper argumrnt.
     */
    public Grouper groupAGrouper(Grouping grouping) {
	Iterator keysI = keySet().iterator();

	while (keysI.hasNext()) {
	    Object gid = keysI.next();
	    Object value = get(gid);

	    if (value instanceof List) {
		put(gid, new Grouper((List)value, grouping));
	    } else if (value instanceof Grouper) {
		((Grouper)value).groupAGrouper(grouping);
	    } else {
		throw new Error("Unexpected class: " +
				value.getClass().getName() +
				" as value in Grouper");
	    }
	}
	return this;
    }

    /**
     * Add an element to a group.
     * @param groupSpecifier the identifier for the group
     * @param obj the object to add to the group
     */
    public void add(Object groupSpecifier, Object obj) {
	List list = getGroup(groupSpecifier);

	// allocate a list if we have to
	if (list == null) {
	    list = new LinkedList();
	    put(groupSpecifier, list);
	}

	// add the object to the list
	list.add(obj);
    }

    /**
     * Get the list of elements associated with
     * a particular group.
     * @param groupSpecifier the identifier for the group
     */
    public List getGroup(Object groupSpecifier) {
	return (List)get(groupSpecifier);
    }
}
