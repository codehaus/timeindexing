// DataType.java

package com.timeindexing.index;

/**
 * An enumeration of well known data types.
 */
public interface DataType {
    /**
     * Nothing has been set
     */
    public final int NOTSET = -1;

    /**
     * The data is of any type.
     */
    public final int ANY = 0;

    /**
     * The data is of unknown type.
     */
    public final int UNKNOWN = 0;

    /**
     * The data is of arbitrary type.
     */
    public final int ARBITRARY = 0;

    /**
     * The data is an integer.
     */
    public final int INTEGER = 1;

    /**
     * The data is a float.
     */
    public final int FLOAT = 2;

    /**
     * The data is a double.
     */
    public final int DOUBLE = 3;

    /**
     * The data is a long.
     */
    public final int LONG = 4;

    /**
     * The data is a short.
     */
    public final int SHORT = 5;

    /**
     * The data is a boolean.
     */
    public final int BOOLEAN = 6;

    /**
     * The data is a byte.
     */
    public final int BYTE = 7;

    /**
     * The data is a char.
     */
    public final int CHAR = 8;

    /**
     * The data is a string.
     */
    public final int STRING = 9;

    /**
     * The data is a void.
     */
    public final int VOID = 10;

    /**
     * The data is a Index Reference.
     */
    public final int REFERENCE = 1000;
}


    
