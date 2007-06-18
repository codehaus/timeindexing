// IndexPropertiesItem.java

package com.timeindexing.appl.playlist;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.appl.IndexPropertiesProcessor;

/**
 * A PlayListItem which is constructed from an Index
 * some IndexProperties.
 */
public class IndexPropertiesItem extends AbstractPlayListItem {
    IndexProperties properties = null;

    IndexPropertiesProcessor proc = null;

    /**
     * Construct the IndexPropertiesItem
     * @param index the Index 
     * @param properties the IndexProperties
     */
    public IndexPropertiesItem(Index index, IndexProperties properties) {
	this.proc = new IndexPropertiesProcessor();
	this.properties = properties;

	setIndex(index);
	setInterval(proc.createInterval(index, properties));
    }

    /**
     * Create a String view of this PlayListItem.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(128);

	buffer.append(getIndex().getURI().toString());
	buffer.append(" => ");
	buffer.append(proc.prettyPrint(properties));

	return buffer.toString();
    }
}
