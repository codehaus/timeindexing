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



// DataReferenceObject.java

package com.timeindexing.index;

import com.timeindexing.basic.Offset;
import com.timeindexing.basic.Size;

/**
 * An implementations for objects that act as references
 * to data from an index.
 * These are used when data is not read into an
 * incore object, but a reference to it is held.
 */
public class DataReferenceObject implements DataReference  {
    /*
     * The offset into a file.
     */
    Offset offset = null;

    /*
     * The size of the data
     */
    Size size = null;

    /**
     * Construct a DataReference
     */
    public DataReferenceObject(Offset offsetR, Size sizeR) {
	offset = offsetR;
	size = sizeR;
    }

    /**
     * Get the offset in the underlying storage.
     */
    public Offset getOffset() {
	return offset;
    }

     /**
     * Get the size of the data in the underlying storage.
     */
    public Size getSize() {
	return size;
    }

    /**
     * String version.
     */
    public String toString() {
	return "DataReferenceObject: offset = " + offset + " size = " + size;
    }
}
