//ManagedIndex.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.Offset;
import com.timeindexing.event.IndexEventGenerator;
import java.util.Properties;

/**
 * An interface for classes that need to manage
 * Indexes.
 * It has the methods needed to manage an index,
 * but are not needed by the application  layer.
 */
public interface ManagedIndex extends ExtendedIndex, IndexEventGenerator  {
    /**
     * Open this index.
     */
     public boolean open(Properties props) throws IndexSpecificationException, IndexOpenException;

    /**
     * Create this index.
     */
     public boolean create(Properties props) throws IndexSpecificationException, IndexCreateException;

    /**
     * Flush this index.
     */
    public boolean flush();

    /**
     * Close this index.
     */
     public boolean close();

    /**
     * Get the headerfor the index.
     */
    public ManagedIndexHeader getHeader();
    
}
