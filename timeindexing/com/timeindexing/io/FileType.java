// FileType.java

package com.timeindexing.io;

/**
 * This has the definitions used in the file header
 * for each of the different files.
 */
public interface FileType {
    /**
     * A header file
     */
    public final byte HEADER = 0x01;

    /**
     * An inline index file
     */
    public final byte INLINE_INDEX = 0x02;

    /**
     * An external-data index file 
     */
    public final byte EXTERNAL_INDEX = 0x03;

    /**
     * An external-data data file
     */
    public final byte EXTERNAL_DATA = 0x04;

    /**
     * An annotations file
     */
    public final byte ANNOTATIONS = 0x05;


    /**
     * T
     */
    public final byte T = 'T';

    /**
     * I
     */
    public final byte I = 'I';

    /**
     * Byte 3
     */
    public final byte BYTE_3 = 0x03;



}


 
