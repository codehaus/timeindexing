// UnitBasedTimestamp.java

package com.timeindexing.time;

/**
 * A unit based timestamp.
 * For all Timestamps that are based on elpased units
 * rather than elapsed time.
 * We need to know the no. of units per second.
 */
public interface UnitBasedTimestamp extends Timestamp {
     /**
     * Get the no of units that have elapsed.
     */
    public long getUnits();

    /**
     * Get the no of units per second, for this kind of timestamp.
     */
    public long getUnitsPerSecond();
}
