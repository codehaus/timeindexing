// DataItemFactory.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * The DataItemFactory is responsible for converting a ByteBuffer
 * into a DataItem, given a specific DataType.
 */
public class DataItemFactory {
    /**
     * Convert a ByteBuffer, given a DataType into a DataItem.
     */
    public DataItem convert(ByteBuffer theBuffer, DataType type) {
	if (type.equals(DataType.STRING)) {
	    return new StringItem(theBuffer);
	} else if (type.equals(DataType.INTEGER)) {
	    return new IntegerItem(theBuffer);
	} else if (type.equals(DataType.BIG_INTEGER)) {
	    return new BigIntegerItem(theBuffer);
	} else if (type.equals(DataType.BIG_DECIMAL)) {
	    return new BigDecimalItem(theBuffer);
	} else if (type.equals(DataType.LONG)) {
	    return new LongItem(theBuffer);
	} else if (type.equals(DataType.SHORT)) {
	    return new ShortItem(theBuffer);
	} else if (type.equals(DataType.FLOAT)) {
	    return new FloatItem(theBuffer);
	} else if (type.equals(DataType.DOUBLE)) {
	    return new DoubleItem(theBuffer);
	} else if (type.equals(DataType.CHAR)) {
	    return new CharItem(theBuffer);
	} else if (type.equals(DataType.BYTE)) {
	    return new ByteItem(theBuffer);
	} else if (type.equals(DataType.BOOLEAN)) {
	    return new BooleanItem(theBuffer);
	} else if (type.equals(DataType.SERIALIZABLE)) {
	    return new SerializableItem(theBuffer);
	} else {
	    return new ByteBufferItem(theBuffer, type);
	}
    }
}
