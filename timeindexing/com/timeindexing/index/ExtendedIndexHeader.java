// ExtendedIndexHeader.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.Offset;

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
     * Get the data style.
     * Either inline or external.
     */
    public int getDataStyle();


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
    public String getIndexURI(ID indexID);

    /**
     * Does this index have the URI of some other index
     */
    public boolean hasIndexURI(String URIName);

    /**
     * Add a new indexID/indexURI
     * @return true, if a new index URI was added; false, if the index had this ID/URI pair already
     */
    public boolean addIndexURI(ID indexID, String URIName);

    /**
     * Get the Offset of the fisrt item.
     */
    public Offset getFirstOffset();

    /**
     * Get the Offset of the last item.
     */
    public Offset getLastOffset();

}
