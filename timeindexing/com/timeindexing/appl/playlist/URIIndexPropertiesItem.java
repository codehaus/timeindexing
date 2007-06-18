// URIIndexPropertiesItem.java

package com.timeindexing.appl.playlist;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.appl.IndexPropertiesProcessor;
import java.net.URI;

/**
 * A PlayListItem which is constructed from an Index URI
 * some IndexProperties.
 */
public class URIIndexPropertiesItem extends AbstractURIPlayListItem {
    IndexProperties properties = null;

    IndexPropertiesProcessor proc = null;

    /**
     * Construct the IndexPropertiesItem
     * @param uri the Index URI
     * @param properties the IndexProperties
     */
    public URIIndexPropertiesItem(URI uri, IndexProperties properties) {
	this.proc = new IndexPropertiesProcessor();
	this.properties = properties;

	setURI(uri);
	setInterval(proc.createInterval(index, properties));
    }

    /**
     * Get the properties.
     */
    public IndexProperties getProperties() {
	return properties;
    }

    /**
     * Create a String view of this PlayListItem.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(128);

	buffer.append(getURI().toString());
	buffer.append(" => ");
	buffer.append(proc.prettyPrint(properties));

	return buffer.toString();
    }
}
