// DataItem.java

package  com.timeindexing.data;

import com.timeindexing.index.DataType;

import java.nio.ByteBuffer;

/**
 * An item of data presented by a data reader into the index.
 * This is the generic interface to the data coming into the system.
 */
public interface DataItem {
    /**
     * Get the data itself
     */
    public ByteBuffer getBytes();

    /**
     * Get the size of the data item
     */
    public long getSize();

    /**
     * Get the DataType of the DataItem.
     */
    public DataType getDataType();

    /**
     * Get the object from a DataItem.
     */
    public Object getObject();

}
