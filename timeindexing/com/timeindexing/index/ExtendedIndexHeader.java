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



// ExtendedIndexHeader.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.Offset;
import java.util.Set;
import java.net.URI;

/**
 * An extendedindex header.
 * This is the interface to an index header whic has extra attributes.
 */
public interface ExtendedIndexHeader extends IndexHeader {
    /**
     * Get the size of the index items.
     */
    public int getItemSize();

    /**
     * Get the size of the data items, if there is fixed size data.
     */
    public long getDataSize();

    /**
     * Get the type name of the things in the data stream.
     */
    public String getDataType(ID typeID);

    /**
     * Does this index have a typed name.
     */
    public boolean hasDataType(String typeName);

    /**
     * Add a new data type
     * @return true, if a new type was added; false, if the index had this ID/typeName pair already
     */
    public boolean addDataType(ID typeID, String typeName);

    /**
     * Does this index have annotations.
     */
    public boolean hasAnnotations();

    /**
     * Get the annotation style.
     * Either inline or external.
     */
    public int getAnnotationStyle();

    /**
     * Get the index URI of a nominated index.
     */
    public URI getIndexURI(ID indexID);

    /**
     * Does this index have the URI of some other index
     */
    public boolean hasIndexURI(URI URIName);

    /**
     * Add a new indexID/indexURI
     * @return true, if a new index URI was added; false, if the index had this ID/URI pair already
     */
    public boolean addIndexURI(ID indexID, URI URIName);

    /**
     * Get the Offset of the fisrt item.
     */
    public Offset getFirstOffset();

    /**
     * Get the Offset of the last item.
     */
    public Offset getLastOffset();

}
