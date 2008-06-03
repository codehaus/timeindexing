Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
