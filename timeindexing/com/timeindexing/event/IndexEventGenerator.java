// IndexEventGenerator.java

package com.timeindexing.event;


/**
 * Any class that needs to fire IndexEvents
 * needs to implement this interface.
 */
public interface IndexEventGenerator {

    /**
     * Add a IndexPrimaryEventListener.
     */
     public void addPrimaryEventListener(IndexPrimaryEventListener l);

    /**
     * Remove a IndexPrimaryEventListener.
     */
     public void removePrimaryEventListener(IndexPrimaryEventListener l);

    /**
     * Add a IndexAddEventListener.
     */
     public void addAddEventListener(IndexAddEventListener l);

    /**
     * Remove a IndexAddEventListener.
     */
     public void removeAddEventListener(IndexAddEventListener l);

    /**
     * Add a IndexAccessEventListener.
     */
     public void addAccessEventListener(IndexAccessEventListener l);

    /**
     * Remove a IndexAccessEventListener.
     */
     public void removeAccessEventListener(IndexAccessEventListener l);


 }
