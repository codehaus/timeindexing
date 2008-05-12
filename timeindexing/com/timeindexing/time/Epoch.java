// Epoch.java

package com.timeindexing.time;

/**
 * Absolute Timestamps have an Epoch.
 */
public interface Epoch extends TimestampFormatting {
    /**
     * Get the name of the Epoch.
     */
    public String getName();
}
