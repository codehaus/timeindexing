/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
     * A shadow index file 
     */
    public final byte SHADOW_INDEX = 0x05;

    /**
     * An annotations file
     */
    public final byte ANNOTATIONS = 0x06;


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

    /**
     * Trailer
     */
    public final long TRAILER = 0x0102030405060708L;

}


 
