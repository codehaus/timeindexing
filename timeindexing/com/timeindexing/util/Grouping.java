// Grouping.java

package com.timeindexing.util;

/**
 * An interface for Grouping a collection.
 * <p>
 * The method getGroup() returns an object 
 * which acts as the identifier for a group.
 * Builtin types cannot be returned, but can be converted
 * to their object types; e.g. int -> Integer.
 * 
 */
public interface Grouping {
    /**
     * Take an object and return an object that specifies
     * the group it is in.
     */
     public Object getGroup(Object obj);
 }
